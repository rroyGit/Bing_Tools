package com.rroycsdev.bingtools;

import android.content.Context;

import android.view.View;


import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class BingDiningMenuTest {

    @Test
    public void getBingWeekDate() {
        String title = "CIW";
        Boolean showProgressDialog = false;
        List<ListItem> listItems = null;
        String link = null;
        View view = null;
        Context context = null;

        BingDiningMenu bingDiningMenu = new BingDiningMenu(link, title, context, listItems, showProgressDialog, view);

        String output = bingDiningMenu.getBingWeekDate(title);
        assertEquals("error", output);
    }
}