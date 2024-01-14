**Similea Alin - 324CA**

## GlobalWaves 3 - Analytics & Recommendations

### Implementation Description:

I used the official solution of stage II as a skeleton.

Compared to the previous stage, for **Monetization** I had to do some
refactoring. I added new fields to the User class in order to keep track of the
bought Merch and the user's plan (Basic/Premium).
Most importantly, for the Basic Users, I had to modify the logic of the
simulation of time in order to integrate the ads. Also, I added some new fields
to the Player class in order to keep track of the ad and its remaining duration
in case the ad doesn't play completely in a single time simulation.
Other than that, everything else is handled by the Monetization class.

For **Notifications**, I created a new class (Notification) and integrated it
in the User class and slightly modified the methods which should trigger a
notification for a user (add Album, Podcast, Merch etc.).

In order to implement the **Recommendations** I integrated the new
functionalities in the HomePage class, because they are the most related to the
normal User's homepage.

For **Page Navigation**, I implemented PageHistory in order to keep track of
the visited pages.

Regarding the used **Design Patterns**, I used the **Factory Method Pattern**
for the creation of the users' Wrapped, because each type of user
(normal/artist/host) has different fields for their Wrapped, so we need to
implement them separately (Artist - WrappedArtist, Host - WrappedHost,
NormalUser - WrappedUser), based on a common interface (Wrapped). In order to
assign the correct Wrapped to a user more easily, I created **WrappedFactory**
that will return the correct Wrapped.

Because we need a unique instance of the CommandRunner, I updated this class by
using the **Singleton Pattern**.

Also, because the PlayerSource has a pretty big number of parameters and 
different modes of instantiating, I used the **Builder Pattern** in order to
create a PlayerSource more easily. A disadvantage of this pattern is that we
need to call a method for each parameter which makes the code larger, but it is
more readable.

The **usual flow of the program** is firstly reading the commands from the
input file and mapping them using CommandInput. Then, we create our library
(Admin) based on the data given as input. For each command, we use the
CommandRunner in order to perform them and create an ObjectNode which will
help us in writing a JSON file.