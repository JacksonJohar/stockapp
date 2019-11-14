package com.johar.Johar_Stocks;

public class StockBuilder {
    private String title,
            content;
    private Float price, change, changpercent;

    public StockBuilder() {
    }

    public StockBuilder(String title, String content, Float price, Float change, Float changepercent) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.change = change;
        this.changpercent = changepercent;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Float getPrice() {
        return price;
    }

    public Float getChange() {
        return change;
    }

    public Float getPercent() {
        return changpercent;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
