package com.rroycsdev.bingtools;

interface BingCrawler {

    void getDiningMenuData(boolean insertUpdateDatabase);

    void makeDailyRequest(String link);
}
