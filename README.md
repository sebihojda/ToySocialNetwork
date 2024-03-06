# ToySocialNetwork

## Introduction
ToySocialNetwork is a semester project developed for the Advanced Programming Methods course at UBB-Info, Year 2, Semester 2. It is an application that has been extended and improved to its current stage, with further expansion plans in progress.

## Features
- **Responsive Graphical Interface**: Implemented using JavaFX.
- **Model-View-Controller Architecture**: Ensures separation of concerns.
- **Observer Pattern**: Enables communication between windows.
- **Layered Architecture**: Enhances modularity and maintainability.
- **Data Persistence**: Achieved through a relational database using PostgreSQL and pgAdmin 4.
- **Dependency Management**: Utilizes Gradle for managing dependencies.
- **Authentication**: Provides Log In/Sign Up features.

## Additional Functionalities
- **Application Feedback**: Provides error messages and suggestive notifications.
- **Data Validation**: Uses regex for data validation.
- **CRUD Operations**: Paginated repositories implement CRUD operations.
- **Controllers**: Implemented for various sections including Log In, Sign Up, AdminPage, and UserPage.

## Future Plans
### Big Stuff
- **Business Enhancements**: Implementing "FancySearch" with dynamic filtering criteria and multi-threading.
- **Undo & Redo Functionality**: Including LogHistory in Admin.
- **Admin Commands**: Adding more commands for user management.

### Controller Improvements
- **UI Customization**: Apply CSS over the graphical interface.
- **Password Change**: Allow users to change their password from the Log In window.
- **Scene Communication**: Enable switching between UserPage and FriendRequest windows.
- **User Information Display**: Add a user information display area to the UserPage window.
- **Statistics**: Introduce statistical graphs for AdminController.
- **Message Viewing**: Implement a feature to view messages for two users in AdminController.

### Testing
- **Unit Testing**: Prioritize unit testing for critical functionalities.

### Comments
- **Documentation**: Add relevant comments for better code understanding.

## Nice To Have (Easy to develop)
- **Validator for Messages**: Enhance validation feedback.
- **Pagination**: Implement paginated repositories for Messages and Accounts.
- **CRUD Operations**: Expand CRUD operations for Accounts.
- **Observer Pattern**: Implement AccountService Observer for updates.

## Known Issues
- **UserController/FriendRequestController Issues**: 
    1. Rejection of friend requests may cause the user to reappear in the list of possible friends.
    2. Removing a friend may not update the other user's window if the conversation is open.
    3. Remove redundant refresh and load buttons.
    4. Decide on implementing or removing search in the FriendRequestPage.

## Video Presentation
- [Link to Video](#)

## Conclusion
Thank you for your patience and interest in ToySocialNetwork. We hope you find the application and its future developments exciting and useful.
```
