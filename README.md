#  SCOUT Mobile (Android) 

## Synopsis

Contains the projects files for the Android version of SCOUT Mobile

SCOUT Mobile is a derivative if NICS Mobile, hence the interchangeability of the names.

##Dependencies

###Android

This is an Android Studio project.

To open the project, open Android Studio and select "Open an existing Android Studio project", then navigate to and select the outermost "SCOUTMobile" directory

To deploy the app:
<lu>
<li>1. Build the app</li>
  <ul>
    <li>Build -> Make Projeect, </li>
  </ul>
<li>2. Run the app</li>
  <ul>
    <li>Run -> Run 'SCOUT_Mobile'</li>
  </ul>
</ul>

## Configuration

Configuration files have been removed from the "SCOUTMobile" project directory, and templates containing placeholder values have been placed in the "config file templates" directory.
All template configuration files have had the ".template" file extension appended to their file names.

Steps required to resolve configuration files to get the projects in working order:
<lu>
<li>1. Go into each template file and populate the placeholder information</li>
<li>2. Remove the ".template" extension from each template file</li>
<li>3. Drag and drop the "config file templates/SCOUTMobile" folder onto the root "SCOUTMobile" folder</li>
  <ul>
    <li>This should merge the folders and add the modified template files into the project directories, restoring the project to working order.</li>
    </ul>
</ul>



###Android

You need to enter your Google Maps API key here which you can register for on Googles developer console: https://developers.google.com/maps/documentation/android-api/signup. That key needs to be placed in the config_strings.xml config file template mentioned above.

You can manually enter your server information into the app after it is built from the settings menu within the app if you would like to change the server at runtime. Or you can edit the config file to store your server info into the config_strings.xml file.

The configuration file can be found at this path: 
SCOUTMobile/SCOUTAndroidAPI/src/main/res/values/config_strings.xml

This is where you will enter all of your SCOUT web configuration information.

The App is setup to allow you to easily toggle between multiple SCOUT instances from the settings menu within the app. If you only have one instance then you can disregard the second item in each string array or remove them. 

SCOUT Mobile is setup to use Application Crash Reports for Android (ACRA-https://github.com/ACRA/acra) to auto send crash reports to a Gmail account of your choice and you can configure this at the bottom of the config_strings.xml