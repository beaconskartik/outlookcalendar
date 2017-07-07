# OutlookCalendar
Simple Calendar with sliding effect and Agenda View showing the events from your calendar.

# About myself
I am a computer Scitienst in Adobe System Pvt. Ltd. currently working in Acrobat Mobile Team (Android)
Play Store link : https://play.google.com/store/apps/details?id=com.adobe.reader&hl=en

More details about me can be found on Llinkedin :https://www.linkedin.com/in/kartiksachan/

## How to build this Project

### Requirements
1) Build Tool Version : 25.0.3
2) Android SDK Version : 25
3) Gradle Version : 2.2.3

Following version are used for support library

    compile 'com.android.support:appcompat-v7:25.3.0'
    compile 'com.android.support.constraint:constraint-layout:1.0.2'
    compile 'com.squareup.retrofit2:retrofit:2.1.0'
    compile 'com.squareup.retrofit2:converter-gson:2.1.0'
    compile 'com.android.support:design:25.3.0'
    
==> Min SDK : 16
==> Compile SDK : 25
==> Target SDK : 25

For wheather, you need to generate a key from site : https://darksky.net/dev/login and put that key in the build.gradle file of Calendar app

### Task supported
./gradlew assemble - Assembles all variants of all applications and secondary packages.
./gradlew assembleAndroidTest - Assembles all the Test applications.
./gradlew assembleDebug - Assembles all Debug builds.
./gradlew assembleRelease - Assembles all Release builds.

## General Overview of Classes

(### Calendar View)
1) #### OutlookCalendarViewPager : It is a infinite view pager (because time can't end) which has a cached a window of size 5 i.e L2, L1 <current month> R1, R2. 
                                  This is responsible for showing custom calender in the application
                                  
2) #### OutlookCalendarViewPagerAdapter : It is used a view pager adapter which pump up the pages to OutlookCalendarViewPager. 
                                         This is responsible for adding months in the view pager.
                          
3) #### OutlookMonthView : This is recycler view which is grid of 7 column and represent the a single month in a calendar.

4) #### OutlookMonthViewAdapter : This is recycler view adapter which populated the data in OutlookMonthView and responsible for binding of data, cursor and also helpfull in syncing data with agenda view

(### Agenda View)

1) #### OutlookAgendaView : It is infinite recycler view which is used to show events in a particular day of the calendar
2) #### OutlookAgendaAdapter : It is used to pump up data to OutlookAgendaView. All things like event, wheather syncin is done inside this class.
3) #### OutlookAgendaCursorAdapter : Fetching data from Calendar URI
4) #### OutlookAgendaEventGroup : Place holder
5) #### OutlookAgendaEventItem 
6) #### OutlookAgendaEventList
  
                              
                                         
