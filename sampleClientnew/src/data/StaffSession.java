package data;
/**
 * Singleton class to manage the current staff session.
 * Stores the logged-in user's username and role (ADMIN or WORKER).
 * Designed for JavaFX desktop applications.
 */
public class StaffSession {

    /**
     * The single instance of StaffSession (singleton pattern).
     */
    private static StaffSession instance = null;

    /**
     * The username of the currently logged-in staff member.
     */
    private String username;

    /**
     * The role of the currently logged-in staff member (e.g., ADMIN or WORKER).
     */
    private Role role;


    /**
     * Returns the singleton instance of StaffSession.
     * Creates a new instance if none exists.
     *
     * @return the singleton instance of StaffSession
     */
    public static StaffSession getInstance() {
        if (instance == null) {
            instance = new StaffSession();
        }
        return instance;
    }

    /**
     * Logs in a staff member by storing their username and role.
     *
     * @param username the username of the staff member
     * @param role     the role of the staff member (ADMIN or WORKER)
     */
    public void login(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    /**
     * Gets the username of the currently logged-in staff member.
     *
     * @return the username of the logged-in user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the role of the currently logged-in staff member.
     *
     * @return the role of the logged-in user
     */
    public Role getRole() {
        return role;
    }

    /**
     * Logs out the current staff member and clears the session.
     */
    public void logout() {
        username = null;
        role = null;
        instance = null;
    }
}
