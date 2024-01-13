**Similea Alin - 324CA**

## GlobalWaves 3 - Analytics & Recommendations

### Implementation Description:

I used the official solution of stage II as a skeleton.

==
For this stage, I firstly refactored the class User because we now have
multiple user types that do different actions. That's why I made the User class
**abstract** and added the NormalUser, Artist and Host that extend it with
their unique methods.
==

Regarding the used Design Patterns, I used the **Factory Method Pattern** for the
creation of the users' Wrapped, because each type of user (normal/artist/host) has
different fields for their Wrapped, so we need to implement them separately
(Artist - WrappedArtist, Host - WrappedHost, NormalUser - WrappedUser), based
on a common interface (Wrapped). In order to assign the correct Wrapped to a user
more easily, I created **WrappedFactory** that will return the correct Wrapped.

Because we need a unique instance of the CommandRunner, I updated this class in
order to use the **Singleton Pattern**.

...

The **usual flow of the program** is firstly reading the commands from the
input file and mapping them using CommandInput. Then, we create our library
(Admin) based on the data given as input. For each command, we use the
CommandRunner in order to perform them and create an ObjectNode which will help
us in writing a JSON file.