Group-Messenger
===================================

Pre-requisites
--------------

- Android Studio
- Android API 19
- Android Virtual Device
- Python



Getting Started
---------------

This project is an implementation of the group messenger app with ordering guarantees. To test the app, clone the whole project and open it in Android Studio.


Testing
-------

Firstly, Install Python and create Android Virtual Devices using the Python script create_avd.py, then run the Python script run_avd.py to boot up the virtual devices. finally, download the test case files from the test folder. 

After the download is completed, open an existing Android Studio project and choose the GroupMessenger2 directory. Then, In the built-in terminal of Android Studio, navigate to the location of where you download your test case files.

Now, type the following command in your terminal (For example: if the system is macOS):
- python set_redir.py 10000
- ./groupmessenger2-grading.osx 

After these commands are executed, the test case will run and verify the correctness of the app.


