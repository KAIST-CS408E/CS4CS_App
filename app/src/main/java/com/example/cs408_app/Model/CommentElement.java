package com.example.cs408_app.Model;

import java.io.Serializable;

public class CommentElement implements Serializable {
    String created_at, contents, _id;
    Author author;

    public CommentElement(String _id, String created_at, String contents, Author author) {
        this._id = _id;
        this.created_at = created_at;
        this.contents = contents;
        this.author = author;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}

