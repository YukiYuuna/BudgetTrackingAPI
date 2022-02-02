## 📚 What I've learned and improved:
* I have considerably improved my skills in working with an array 😉 of Data Structures.
* Object-Relational Mapping and general workflow with databases (mainly PostgreSQL🐘)
* Working with Spring 🍃 and lots of its features, including Spring Security🔐 , Spring Boot and many more.
* How to work with JWT tokens (more spec. OAuth tokens).
* The concepts of Authorization & Authentication as well as implementing them.
* Learned and implemented as much as possible the RESTful principles.
* Bettered my understanding on the concept of abstraction.
* Learned how to write complex JUnit Tests📝, using a BDD approach and utilizing tools like Mockito and MockMvc.
* Improved my skills with Version Control (Git).
* Working with Postman and how to document an API/

## Lets look at the API and its functionalities 👇

### Authentication
All REST Endpoints (except endpoints for registration and login) are secured and must be authenticated, using a JWT token that is generated upon successfull login. This JWT token has a set expiration time ⏳ and when it times out, the token becomes invalid. 

### Authorization
The API has two roles, one being the admin, who has access to all endpoints except those that make changes to the users information (meaning, no PUT, POST, DELETE for the admin 😅), and the other being the user, who has access to endpoints that show, update or create ONLY data correlated with the logged-in user.

### REST Endpoints
The Budget Tracking API covers all CRUD operations, meaning - POST, GET, PUT, DELETE.
#### 🚨 Click the button to run the API on Postman! I've set up all operations and descriptions on what each enpoint does ‼
[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/5dd457ac0ef043e168a3?action=collection%2Fimport)

This way, the setup for executing the API won't be such a hastle, as all endpoints are pre-written and documented.

## 📪 Using Postman to run operations
❗ <strong>I am writing this just in case. Everything, except the token😀, is pre-written in Postman!</strong> ❗

1️⃣First of all, in order to register, go to the <strong>POST/api/register </strong> end-point and type-in your info.</br>
<img src="https://user-images.githubusercontent.com/76811860/152228214-d43917f4-39b8-4fb4-9056-00ecfd345b4e.gif" width="75%" height="75%"/>

2️⃣Second, go to the <strong>GET/api/login</strong> and put your credentials. You will be provided with 'acces_token', which is used to enter every operation you do as a user.</br>
<img src="https://user-images.githubusercontent.com/76811860/152227937-94e8db04-a827-4fe1-b256-2147d81435e4.gif" width="75%" height="75%"/>

3️⃣From then on, for every operation you want do, add the 'access_token' in the "Headers" section, as a Authorization param (key="Authorization", value="access_token").</br>

#### 🎥 Here is a little demo on how to configure and run the endpoints, using the acces_token:

> <strong>GET /expense/transactions</strong>
<img src="https://user-images.githubusercontent.com/76811860/152227965-0c46df74-89c2-4dd6-aa4a-6415224c5c3b.gif" width="75%" height="75%"/>

> <strong>POST /expense/transaction</strong>
<img src="https://user-images.githubusercontent.com/76811860/152227970-f777ea18-224c-4a93-8682-f7bd10f9f7b3.gif" width="75%" height="75%"/>

## 📝 TO-DOs for this project:
* 👓 Although, I prefer working on the back-end and my focus in general is there, I would love to create a front-end for this API.
* ⚡ Potentially redesign the code to handle a bigger set of data with better performance (I know the API can handle a lump sum of data, but performance can definitely be improved)

## 👨‍💻 Technologies and Versions I've used
* Java SDK - version: 17
* Spring Boot Framework, Spring Security - version: 2.6.0
* PostgreSQL - version: 42.3.1
* JWT (Auth0) - version: 3.18.2
* Lombok - version: 1.18.22

## 💻 Setting up and running the API on your local machine:
1. First, make sure you have your java and maven versions configured correctly on your machine.
2. Run:
& git clone <copy & paste the HTTP URL from GitHub>
.. in a place on your computer, where you find comfortable.
3. Setup the  'application.properties' based on your PostgreSQL and localhost details.
4. At this point, you should be able to run the 'ExpenseTrackerApplication.main' and with that start the API. 🥳

## ❤ Acknowledgements
- Thank you, Ivan Duhov (https://github.com/IvanDuhov) , for the contribution to the project and the motivation to improve my coding skills each day little by little!
- Also, big thanks to uncle Google! ❤ Couldn't live without you.

## 💬 Contributions & Suggestions
I would love for you to check the API and if you have any suggestions or tips on its improvement, send me a message on LinkedIn:
* https://www.linkedin.com/in/kbor/ 

And if you want to contribute to this project, feel free to add a branch, develop and request your changes!
