Arch_a3
=======

**Bolded** text indicates cmd commands.

Setup
---------------------------------- 
To set up the system, in the root directory run the compile batch script:
 	
**compile**
 	
You should only need to run this once. 

Running the Systems
---------------------------------- 
To run any of the systems, you will need to kick off the event manager 
and then start the system of interest (A, B, or C) up. 

This will require two commands (one to start the event manager and the next to start the system):

1. Start the event manager before running any systems by running the EMStart batch script:

**EMStart**

2. Start system A, B, or C, by running the appropriate batch file.

To run system A:
**StartSystemA**

To run system B:
**StartSystemB**

To run system C:
**StartSystemC**

Shutting Down the Systems 
---------------------------------- 
To shut the system down, you can close all the windows individually. This is painful.
You can also kill all the java processes on your machine through the command line:
If you're okay with killing all java applications on your machine, run the following two commands:

**taskkill /im java.exe**

**taskkill /im rmiregistry.exe**

Doing this is easier than closing all the windows, 
but will require you to restart the event manager before starting up another system. 
