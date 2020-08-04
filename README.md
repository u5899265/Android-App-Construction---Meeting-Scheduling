**See Wiki for detailed descriptions of this APP!**


Brainstorm ᕕ( ᐛ )ᕗ

To start the development, clone this repo.
In Andriod Studio (AS), select "Open an existing Android Studio project" and select the root folder "comp2100_6442_Meeting_Scheduling". AS will start a gradle build. It should proceed with no error.

Meetings, Meeting, TimeSlot and User are data structures designed to hold our data
We also used LocalDateTime and Duration from java.time package added since jdk8

Submit and FetchByOwner are AsyncTasks which does a network related operation

Classes end with TypeAdaptor are used by gson in the serialisation and deserialisation of data.

CreateMeetingActivity is entered from the FirstActivity, finished if a new Meeting is successfully submitted.
