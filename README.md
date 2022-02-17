# tenmo

This application served as a final project for the second module of Tech Elevator. It is a mock online payment service for transferring "TE bucks" between friends. This utilizes a RESTful API server and a command line interface for user interaction. 

Users are able to:
Register with a username/password which grants them an initial balance of 1000 TE Bucks
Login with the same username/password which provides the user a token that is used to authorize future actions.
Send a specific amount of Bucks to another user
Request a specific amount of Bucks from another user
Approve/Reject transfer requests
View history of all of their transactions, with additional details for each transaction including any pending transfers

The command line interface uses an API (As of this writing, to localhost) to communicate with a PostgreSQL database that stores all user and transfer information

