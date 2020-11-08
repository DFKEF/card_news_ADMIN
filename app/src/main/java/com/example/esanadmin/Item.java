package com.example.esanadmin;

public class Item {
    String user,bitmap,title,date,subject,content,getid;
    int imgnum;

    public Item(String user, String bitmap, String title, String date, String subject, String content, int imgnum, String getid) {
        super();
        this.user = user;
        this.bitmap = bitmap;
        this.title = title;
        this.date = date;
        this.subject = subject;
        this.content = content;
        this.imgnum = imgnum;
        this.getid = getid;
    }
}
