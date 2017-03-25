package ro.softvision.androidworkshop.model;

public class GithubProfile {
    private String mName;
    private String mOrganization;
    private String mImageUrl;
    private String mBio;
    private String mLocation;
    private String mEmail;
    private String mCreated;
    private String mUpdated;
    public Integer mNumPublicRepos;
    public Integer mNumPrivateRepos;
    public String mBlogUrl;

    public static GithubProfile MockGithubProfile;

    static {
        MockGithubProfile = new GithubProfile();
        MockGithubProfile.setName("Octocat");
        MockGithubProfile.setOrganization("GitHub");
        MockGithubProfile.setBio("I am a Senior Android Cat-gineer with six years worth of experience " +
                "in developing Android applications. My passion always resided in creating beautiful " +
                "applications, and researching for new ways to improve user eperience for mobile devices. " +
                "I see myself as an effective leader, skilled in enlisting the support of my team " +
                "members so they are aligned with the project's and organisation's goals. " +
                "Using a meticulous and detail-oriented approach, I am able to prioritise and delegate " +
                "tasks effectively, to ensure project completion under the best of terms.");
        MockGithubProfile.setLocation("Bucharest");
        MockGithubProfile.setEmail("octocat@github.com");
        MockGithubProfile.setCreated("Thu, May 26, 2015");
        MockGithubProfile.setUpdated("Wed, Jun 03, 2016");
        MockGithubProfile.setNumPublicRepos(23);
        MockGithubProfile.setNumPrivateRepos(7);
        MockGithubProfile.setBlogUrl("someblog.net");
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getOrganization() {
        return mOrganization;
    }

    public void setOrganization(String organization) {
        mOrganization = organization;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getBio() {
        return mBio;
    }

    public void setBio(String bio) {
        mBio = bio;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    public String getUpdated() {
        return mUpdated;
    }

    public void setUpdated(String updated) {
        mUpdated = updated;
    }

    public Integer getNumPublicRepos() {
        return mNumPublicRepos;
    }

    public void setNumPublicRepos(Integer numPublicRepos) {
        mNumPublicRepos = numPublicRepos;
    }

    public Integer getNumPrivateRepos() {
        return mNumPrivateRepos;
    }

    public void setNumPrivateRepos(Integer numPrivateRepos) {
        mNumPrivateRepos = numPrivateRepos;
    }

    public String getBlogUrl() {
        return mBlogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        mBlogUrl = blogUrl;
    }
}
