# Assignment Manager


This is an assignments manager which are created using Javafx1.8 (But later migrated to Javafx14). The project is created using the Maven to manage the build process. The project used Derby, which is an RDBMS that could be used with the Java program. The program could let its user be able to distinguish the available assignment by sorting the assignment according to their due date. As well as the different background colour for different assignment status (still got time left, already due, and almost times up)


# Setup

#### Note that you need to make sure you have Java version 14 as I use it for this project


## Cloning the project

To setup the project you'll first need to clone the project. you can run this command in the location that you want to store this project in :
```
git clone https://github.com/Leonlit/Assignment_manager.git
```

Then wait for the process to complete.
Once the cloning process is complete, you now can open up the project folder in any IDE that supports Java.


## Database configuration

The database information can be found in the DBManagement.java. You can change the username and password to your database according to your database configuration. But if you've decided to create a new database for this project, you can either follow my database configuration or create your own one. Just make sure that you change the value of HOST, USER, and PASSWORD located in the DBManagement.java.

Once you created a database, you'll now need to execute an query so that we can have a table to store our data.

The SQL query:
```
CREATE TABLE Assignments (    
   "ID" INT not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),   
   "TITLE" LONG VARCHAR,     
   "DUEDATE" DATE   
);
```

After that, you can now start up your database server and then try and run the project. It should be working right away


## Screenshots

### Start-up
![start-up of the program](https://github.com/Leonlit/Assignment_manager/blob/master/img/start_up.png?raw=true)

### Example when there's record in the database
![example view when there's record in database](https://github.com/Leonlit/Assignment_manager/blob/master/img/example_view.png?raw=true)

### Adding new Assignment into the database
![example view when there's record in database](https://github.com/Leonlit/Assignment_manager/blob/master/img/add_new.png?raw=true)

### Editing existing Assignment data
![example view when there's record in database](https://github.com/Leonlit/Assignment_manager/blob/master/img/edit.png?raw=true)


## Contact
If you have any question or suggestion regarding the project, feel free to contact me. If you found any bugs in the program, please contact me if you can. Thanks :D 

### [leonlit](https://github.com/Leonlit) :
 - [twitter](https://twitter.com/leonlit)
 - [email](leonlit123@gmail.com)
