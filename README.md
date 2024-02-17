## GlobalWaves

GlobalWaves is a Spotify-like platform emphasizing object-oriented design,
strategically implementing 3 design patterns for a robust foundation for
scalability and future expansion.

### Implementation Description:

The **usual flow of the program** is firstly reading the commands from the
input file and mapping them using CommandInput. Then, we create our library
(Admin) based on the data given as input. For each command, we use the
CommandRunner in order to perform them and create an ObjectNode which will
help us in writing a JSON file.

The project includes mechanisms for an Audio Player simulation for multiple
simultaneous users, Monetization for artists, Notifications, Page
Navigation, Recommendations based on different criteria and more.

### Design Patterns Used:

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
