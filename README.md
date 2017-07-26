#  SCOUT Mobile (Android) 

## Synopsis

Contains the projects files for the Android version of SCOUT Mobile

SCOUT Mobile is a derivative if NICS Mobile, hence the interchangeability of the names.

##Dependencies

###Android

SCOUT Mobile is only compatible with Eclipse

When creating your Eclipse workspace, import all the packages in the "nics-mobile/android" directory
  <br>- Right click the project explorer window
  <br>- Select Import then in the next menu select import again
  <br>- In the import open the Android dropdown and select Existing Android Code into Workspace
  <br>- Browse to the android directory of the repository and press the open button with the android folder selected
  <br>- You should see the following 8 packages in the import window. Make sure all are checked and press the finish button

<ul>
  <li>LoginActivity</li>
  <li>nicsAndroidAPI</li>
  <li>android-async-http</li>
  <li>google-play-services_lib</li>
  <li>google-support-v7-appcompat</li>
  <li>gridlayout_v7</li>
  <li>nasa-worldwind-coordinate-converter</li>
  <li>nmea-handler_lib</li>
</ul>

Now that everything is imported you need to configure the dependencies of each package.

The NicsMobile and NicsAndroidAPI package dependencies should be preconfigured in each package's properties menu when imported to match the structure below. You can check these by right clicking each NICS project and selecting Properties then Android.

LoginActivity
<ul>
  <li>nicsAndroidAPI</li>
  <li>nmea-handler_lib</li>
  <li>gridlayout_v7</li>
  <li>google-support-v7-appcompat</li>
</ul>

NICSAndroidAPI  (Is Library)
<ul>
  <li>android-async-http</li>
  <li>google-play-services_lib</li>
  <li>gridlayout_v7</li>
  <li>nasa-worldwind-coordinate-converter</li>
</ul>

You can deploy the app by building the "LoginActivity" project as an Android Application in Eclipse.


## Configuration

Configuration files have been removed from the "android" project directory, and moved to the "config file templates" directory.
All configuration files have had the ".template" file extension appended to their file names.

Steps required to resolve configuration files to get the projects in working order:
<lu>
<li>1. Go into each template file and populate the template information</li>
<li>2. Remove the ".template" extension from each template file</li>
<li>3. Drag and drop the "config file templates/android" folders onto the root "android" folder</li>
  <ul>
    <li>This should merge the folders and add the modified template files into the project directories, restoring the projects to working order.</li>
    </ul>
</ul>



###Android

You will also need to enter your Google Maps API key here which you can register for on Googles developer console: https://developers.google.com/maps/documentation/android-api/signup. That key needs to be placeing in the config_strigns.xml file mentioned below.

You can manually enter your server information into the app after it is build from the settings menu within the app if you would like to change the server at runtime. Or you can edit the config file to store your server info into the config_strings.xml file.

The configuration file can be found at this path: nics_mobile/android/NICSAndroidAPI/res/values/config_strings.xml

This is where you will enter all of your NICS web configuration information.

The App is setup to allow you to easily toggle between multiple NICS instances from the settings menu within the app. If you only have one instance then you can disregard the second item in each string array or remove them. 

NICS Mobile is setup to use Application Crash Reports for Android (ACRA-https://github.com/ACRA/acra) to auto send crash reports to a Gmail account of your choice and you can configure this at the bottom of the config_strings.xml
