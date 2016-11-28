# Chat-App

# The Project

This project aims to develop a Client/Server chat system where clients can connect to the server and use it to send each other messages.

# Components and Featues

The chat system consists of some of the basic functions one would find in a chat application. This includes a text box for inputting the user messages before sending. In order to send a message, we’ve implemented a “Send All” button for users to push and deliver the text into the chat screen where everyone can see (the people clients connected, at least). We’ve also implemented a “Send Private” button, in which when it is pressed, another client’s username can be entered to send a message privately to them. If the name entered does not match any currently connected clients, a message will be output which states that this client name was not found. In the chat screen, it should indicate who the message come from in order to differentiate a client from another. We’ve also dedicated a space in the application to inform the clients on who else are connected in the server. If a user clicks the exit button at the top right, they will be asked if they wish to disconnect. If so they will be removed from the client list. 

# Client-Server Protocol

To initiate point-to-point client/server connection, the Transmission Control Protocol (TCP) is used. TCP is a reliable protocol, and it guarantees the delivery of the data packets it transmits. If the data is corrupted or lost, TCP will resend the data until it confirms that the packets have been successfully transmitted. TCP is needed for applications that must reliably send messages back and forth to ensure that a perfect copy of the data arrives at the other side – uncorrupted.
The chat system will also incorporate User Datagram Protocol (UDP) for updating the clients on who is currently in the chat room. UDP is used for purposes where error-checking and correction are unnecessary. It is suitable for simple query response protocols just like the mentioned feature where the client is given information about the users who are connected to the chat server.

# User Documentation

To start a connection, first someone must start the server. After this many client threads can be started. Once a client starts to run, the user will be given a prompt to enter the name they want to go by. They are then asked to enter an IP address to connect to (if they are running the server they can enter “localhost” or on multiple computers they must use the hosts IP address). The client would then be given their own chat application screen; which includes a text box to type the messages in, the public screen where the message are broadcasted, a send all button, a send private button and a side box with the list of other clients that are connected. If a client wishes to leave they can click the exit (“X”) at the top right to disconnect.

