
>Note that application was made in learning JavaFX proccess and (for sure) contains many anti-patterns. Most of the code has comments for better understanding 
and pointing it's main weakness to improve. Hence it is using OpenJFX 14 and preview of Java 14, make sure you configured your enviroment right.  

# JFXMail

Project is made by going through [Advanced Java programming with JavaFx: Write an email client](https://www.udemy.com/course/advanced-programming-with-javafx-build-an-email-client/)
course by Alex Horea at Udemy. 

Although I have made some changes, some 'upgrades' in my opinion and 'decorated' code with explanations for better understanding. JFXMail allows you to
connect with your email account (GMail only for now) and download all your emails (READ-ONLY). Then you can send an email which can contain multiple attachments. Application does not save any emails 
on the device so you have to configure your email account and download messages on every start. Also make a note that it downloads ALL THE EMAILS from the server.

## Application details

* Table with emails
    * Contains emails downloaded from the server
    * Sorting is implemented
    * Selecting message marks it as `unread = false`
    * Context menu is available
        * Is active only when message is selected
        * Use it for mark message as `unread = true`
        * Use it for displaying message in new window

>Note that application downloads emails in READ-ONLY parameter so changing its' status of `unread` is just in-app change

* Email view
    * Displays message
    * HTML support is implemented
* Left-side accounts and folders view
    * Contains account tree-view of it's folders
    * Folders are created based on server-side structure
    * There can be multiple accounts added (there is no validation for same account adding)
* Buttons and labels at the top
    * `Create an email` displays new window for email creation
        * There can be multiple windows of this type opened
        * Window will not pop-up if none account is created
    * `Add new account` displays new window for creating account
        * There can be only one window of this type opened
        * Window is dependent on the main view which is unavailable until this one is opened
        * Default data is set for simplier and faster use during implementation proccess
        
        > Default data is passed through environment variables such as `sampleEmailAddress` for account's login/mail and `sampleEmailPassword` for it's password. If you will not provide them, labels will not contain any default data. 
       
        * Successful account creation causes `Email account constructed successfully` displayed in console, creating folders and downloading all it's messages 
            * `Icon not found: null` message in console is irrelevant and caused of icon creating implementation
            * After at least one created account you are able to `Create an email`
        * Unsuccessful account creation causes `Email account construction failed` displayed in console
    * `Read/Unread message` changes selected email's status
    * `Download attachments` downloads selected email's attachments (if available) 
        * Files are saved at hard-coded location `D:\Downloads\`
        * Progress of downloading is displayed in label and progressbar next to `Download attachments` button
        * Progress of downloading is displayed in console

>Note that the same messages in different folders are treated as different messages so changing it's `unread` status in one folder has not an impact to the same message in another folder


## Built using

* Java JDK 14
* [OpenJFX](https://openjfx.io/index.html)
* [Maven](https://maven.apache.org/)
* [JavaMail](https://javaee.github.io/javamail/)

>Note that there are other dependencies in the `pom.xml` file like `Project Lombok`, `MapStruct` and `controlsfx`  but finnaly they were not used and can be safetly deleted

## Authors

* **Dawid Sekuła** - [GitHub](https://github.com/dawidsekula)
