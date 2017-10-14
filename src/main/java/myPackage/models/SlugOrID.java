package myPackage.models;

public class SlugOrID {

    public Integer id = null;
    public String slug = null;
    public boolean IsLong = true;

    public SlugOrID(String slugOrId) {
        try {
            id = Integer.parseInt(slugOrId);
            IsLong = true;
        } catch (Exception e) {
            slug = slugOrId;
            IsLong = false;
        }
    }
}
