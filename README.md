**See Wiki for detailed descriptions of this APP!**
https://github.com/u5899265/Android-App-Construction---Meeting-Scheduling/wiki


Brainstorm ᕕ( ᐛ )ᕗ

Meetings, Meeting, TimeSlot and User are data structures designed to hold our data
We also used LocalDateTime and Duration from java.time package added since jdk8

Submit and FetchByOwner are AsyncTasks which does a network related operation

Classes end with TypeAdaptor are used by gson in the serialisation and deserialisation of data.

CreateMeetingActivity is entered from the FirstActivity, finished if a new Meeting is successfully submitted.
