package com.jingoal.qyq.common;

/**
 * Created by lianghb on 16/3/11.
 * Description:
 */
public class Feed implements Keyable {

    private static final long serialVersionUID = 1L;
    private long id;
    private long time;
    private long cid;
    private String content;

    public String key() {
        return this.getClass().getCanonicalName() + "_" + id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feed)) return false;

        Feed feed = (Feed) o;

        return getId() == feed.getId();

    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }
}
