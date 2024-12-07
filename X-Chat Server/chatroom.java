public class chatroom {
    private List<String> roomusers;

    private boolean isPrivate;
    private String name;

    public chatroom(List<String> users, String name, boolean isPrivate)
    {
        this.roomusers = new List<>();
        this.isPrivate = isPrivate;
        this.name = name;
        users.toFirst();
        while(users.hasAccess())
        {
            roomusers.append(users.getContent());
            users.next();
        }

    }

    public void addUser(String pUsername)
    {
        roomusers.append(pUsername);
    }

    public void rmUser(String pUsername)
    {
        roomusers.toFirst();
        while(roomusers.hasAccess())
        {
            if(roomusers.getContent().equals(pUsername))
            {
                roomusers.remove();
            }
            roomusers.next();
        }
    }

    public List<String> getUsers()
    {
        return roomusers;
    }

    public boolean getPrivate()
    {
        return isPrivate;
    }

    public void setName(String pName)
    {
        this.name = pName;
    }

    public String getName()
    {
        return name;
    }
}

