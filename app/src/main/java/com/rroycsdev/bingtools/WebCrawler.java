package com.rroycsdev.bingtools;
import com.rroycsdev.bingtools.BingDiningScrapper.DiningDataScrapper;

interface WebCrawler {

    DiningDataScrapper getDiningMenuData(boolean isInsertDatabase);

    void makeDailyRequest(String link);
}
