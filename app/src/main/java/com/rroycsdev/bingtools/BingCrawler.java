package com.rroycsdev.bingtools;
import com.rroycsdev.bingtools.WebScrapper.DiningDataScrapper;

interface BingCrawler {

    DiningDataScrapper getDiningMenuData(boolean insertUpdateDatabase);

    void makeDailyRequest(String link);
}
