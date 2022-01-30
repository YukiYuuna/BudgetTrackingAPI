package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.AbstractTest;
import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.service.TransactionServiceImpl;
import com.rigel.ExpenseTracker.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest extends AbstractTest {

    @MockBean
    UserServiceImpl userService;
    @MockBean
    TransactionServiceImpl transactionService;
    @MockBean
    BCryptPasswordEncoder encoder; //needed for configuration of API security

    MockMvc mockMvc;
    @Autowired WebApplicationContext webApplicationContext;

    User firstUser = getNormalUser();
    User secondUser = new User(888L, "desi", "desi", "Desi", "Popova", "desippv@gmail.com", 5000.0);

    final List<ExpenseTransaction> transactions = listOfTransactions(firstUser, secondUser);
    final String categoryType = "expense";
    final String date = "2021-10-10";
    final String categoryName = "food";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/api").with(user("koko").password("koko").roles(Role.USER, Role.ADMIN)))
                .apply(springSecurity())
                .build();
        secondUser.setRoles(Set.of(new Role(Role.USER)));
    }


    @Test
    void fetchingAllUserTransactions() throws Exception {
//        given
        Pageable pageable = PageRequest.of(1, 2);
        List<ExpenseTransaction> userSpecificTransactions = transactions.stream()
                .filter(t -> t.getUser().getUsername().equals("koko"))
                .collect(Collectors.toList());
        Page<ExpenseTransaction> pageOfTransactions =
                new PageImpl<>(userSpecificTransactions, pageable, userSpecificTransactions.size());
        HashMap<String, Object> wantedResult = new HashMap<>();
        wantedResult.put("username", "koko");
        wantedResult.put("totalTransactions", pageOfTransactions.getTotalElements());
        wantedResult.put("totalPages", pageOfTransactions.getTotalPages());
        wantedResult.put("transactions", pageOfTransactions.getContent());

        given(transactionService.getAllUserTransactions(any(Pageable.class), any(String.class))).willReturn(wantedResult);

//        when
        MvcResult result = mockMvc.perform(get("/api/expense/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentPage", "1")
                        .param("perPage", "2"))
                        .andExpect(status().isOk()).andReturn();

//        then
        assertThat(result).isNotNull();
        verify(transactionService, atLeast(1)).getAllUserTransactions(any(Pageable.class), any(String.class));
        ArrayList<?> providedTransactions = (ArrayList<?>) mapFromJson(result.getResponse().getContentAsString(), LinkedHashMap.class).get("transactions");
        int index = 0;

        for (Object transaction: providedTransactions) {
            LinkedHashMap<String, Object> t1 = (LinkedHashMap<String, Object>) transaction;
            assertThat(t1.get("expenseAmount")).isEqualTo(userSpecificTransactions.get(index).getExpenseAmount());
            assertThat(t1.get("categoryName")).isEqualTo(userSpecificTransactions.get(index).getCategoryName());
            assertThat(t1.get("date")).isEqualTo(userSpecificTransactions.get(index).getDate().toString());
            index++;
        }
    }

    @Test
    void fetchingAllExpenseCategoriesOfTheUser() throws Exception {
//        given
        Set<ExpenseCategory> categories = new HashSet<>();
        for (ExpenseTransaction t: transactions) {
            categories.add(t.getExpenseCategory());
        }
        HashMap<String, Object> wantedCategories = new LinkedHashMap<>();
        wantedCategories.put("username", "koko");
        wantedCategories.put("totalCategories", categories);
        given(transactionService.getCategories(any(String.class))).willReturn(wantedCategories);

//        when
        MvcResult result = mockMvc.perform(get("/api/expense/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();
//        then
        assertThat(result).isNotNull();
        verify(transactionService, atLeast(1)).getCategories(any(String.class));
        ArrayList<?> providedCategories = (ArrayList<?>) mapFromJson(result.getResponse().getContentAsString(), LinkedHashMap.class).get("totalCategories");

        int index = 0;
        for (Object category: providedCategories) {
            LinkedHashMap<String, Object> c1 = (LinkedHashMap<String, Object>) category;
            assertThat(categories.stream().anyMatch( c -> c.getCategoryName().equals(c1.get("categoryName")))).isTrue();
            index++;
        }
    }

    @Test
    void fetchingTransactionById() throws Exception {
//        when
        mockMvc.perform(get("/api/expense/transaction/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

//        then
        then(transactionService).should(atMost(1)).getTransactionById(any(String.class),any(Long.class));
    }

    @Test
    void fetchingTransactionByDate() throws Exception {
//        given
        Pageable pageable = PageRequest.of(0, 5);
        given(userService.numberOfUsers()).willReturn(5);

//        when
        mockMvc.perform(get("/api/expense/transactions/date")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", date)
                        .param("currentPage", "1")
                        .param("perPage","2"))
                .andExpect(status().isOk());

//        then
        then(transactionService).should(atMost(1)).getTransactionByDate(pageable, date, categoryType);
    }

    @Test
    void fetchingTransactionByCategory() throws Exception {
//        given
        Pageable pageable = PageRequest.of(0, 5);
        given(userService.numberOfUsers()).willReturn(5);

//        when
        mockMvc.perform(get("/api/expense/transactions/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("category", "food")
                        .param("currentPage", "1")
                        .param("perPage","2"))
                .andExpect(status().isOk());

//        then
        then(transactionService).should(atMost(1)).getTransactionsByCategoryAndUsername(pageable, categoryType, categoryName);
    }

    @Test
    void addingExpenseCategoryToDB() throws Exception{
//        given
        ExpenseCategory expenseCategory = new ExpenseCategory(categoryName, firstUser);
        expenseCategory.setExpenseCategoryId(1L);

//        when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/add/expense/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(expenseCategory))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
//        then
        then(transactionService).should(atMost(1)).addCategory(categoryName, categoryType);
        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Expense category has been saved successfully!");
    }

    @Test
    void addExpenseTransaction() throws Exception{
//        given
//        when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/add/expense/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", date)
                        .param("expenseAmount", "90.2")
                        .param("categoryName", categoryName)
                        .param("description", "Grocery bill")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
//        then
        then(transactionService).should(atMost(1))
                .addTransaction(categoryType, date, 90.2, categoryName, "Grocery bill");
        assertThat(result).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Transaction added successfully!");
    }

    @Test
    void modifyExpenseCategory() throws Exception {
//        given
        User user = new User(9L, "koko", "koko", "Koko", "Bor", "kbor@gmail.com", 500.0);
        ExpenseCategory category = new ExpenseCategory(1L,categoryName,user);
        ExpenseTransaction transaction = new ExpenseTransaction(300.0, categoryName, "Grocery bill", user);
        transaction.setExpenseTransactionId(1L);
        transaction.setExpenseCategory(category);
        user.setExpenseTransactions(List.of(transaction));
        ExpenseTransaction modTransaction = new ExpenseTransaction( 600.2, categoryName, "Big bill", firstUser);
        transaction.setExpenseCategory(category);

        given(transactionService.transactionExists(categoryType, 1L)).willReturn(true);
        given(transactionService.categoryExists(categoryType, categoryName)).willReturn(true);
        when((Optional<ExpenseCategory>)transactionService.getCategory(categoryType, categoryName)).thenReturn(Optional.of(category));
        when((Optional<ExpenseTransaction>)transactionService.getTransactionById(any(),any())).thenReturn(Optional.of(transaction));

//        when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/modify/expense/transaction/{transactionId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(modTransaction))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
//        then
        then(transactionService).should(atMost(1)).saveTransactionToDB(LocalDate.parse(date),
                300.0, categoryName, "Grocery bill", categoryType);
        ExpenseTransaction providedTransaction = mapFromJson(result.getResponse().getContentAsString(),ExpenseTransaction.class);
        assertThat(providedTransaction).isNotNull();
        assertThat(providedTransaction.getExpenseAmount()).isEqualTo(modTransaction.getExpenseAmount());
        assertThat(providedTransaction.getCategoryName()).isEqualTo(modTransaction.getCategoryName());
    }

    @Test
    void modifyExpenseTransaction() {
//        given
//        when
//        then
    }

    @Test
    void deleteExpenseCategory() {
//        given
//        when
//        then
    }

    @Test
    void deleteAllUserExpenseTransactionsByCategory() {
//        given
//        when
//        then
    }

    @Test
   void deleteAllUserExpenseTransactions() {
//        given
//        when
//        then
    }

    @Test
    void deleteExpenseTransactionById() {
//        given
//        when
//        then
    }
}