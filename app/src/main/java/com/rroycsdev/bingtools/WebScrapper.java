package com.rroycsdev.bingtools;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class WebScrapper implements BingCrawler {

    private BingDiningMenu context;

    WebScrapper(BingDiningMenu context) {
        this.context = context;
    }

    @Override
    public void getDiningMenuData(boolean insertUpdateDatabase) {
        new DiningDataScrapper(context).execute(insertUpdateDatabase);
    }

    @Override
    public void makeDailyRequest(String link) {
        new NewMenuRequest(context).execute(link);
    }

    //make async web request using JSOUP
    //first parameter (Boolean), if true update database - if false insert into database
    private static class DiningDataScrapper extends AsyncTask<Boolean, Void, Void> {
        private StringBuilder stringBuilderBreakfast = new StringBuilder();
        private StringBuilder stringBuilderLunch = new StringBuilder();
        private StringBuilder stringBuilderDinner = new StringBuilder();

        private String weekString;
        private ProgressDialog pD;
        private Boolean updateDatabase = false;
        private Boolean loadEmptyMenu = false;
        private String errorMessage = "";


        //weak reference
        private WeakReference<BingDiningMenu> activityReference;

        DiningDataScrapper(BingDiningMenu context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            BingDiningMenu bingDiningMenu = activityReference.get();

            if(bingDiningMenu.showProgressDialog && bingDiningMenu.diningMenuView.getWindowToken() != null) {
                pD = new ProgressDialog(bingDiningMenu.context);
                pD.setCancelable(false);
                pD.setMessage("Loading menu, please wait.");
                pD.show();
            }
        }

        @Override
        protected Void doInBackground(Boolean... params) {

            final BingDiningMenu bingDiningMenu = activityReference.get();
            if (params.length > 0) updateDatabase = params[0];

            if (bingDiningMenu == null) {
                loadEmptyMenu = true;
                return null;
            }

            try {
                // test for valid connection
                Connection.Response response = Jsoup.connect(bingDiningMenu.link).timeout(5000).execute();
                if (response.statusCode() != 200) {
                    loadEmptyMenu = true;
                    return null;
                }

                Document doc2 = Jsoup.connect(bingDiningMenu.link).get();
                Element body = doc2.body();
                Element menuDiv = body.getElementById("bite-menu");
                String menuActive = menuDiv.getElementById("bite-calc").select("p").text();

                if (menuActive.equals("Sorry, no menu found") || menuActive.contains("not found")
                        || menuActive.contains("no menu")) {
                    loadEmptyMenu = true;
                    errorMessage = "One or more dining halls are closed, no menu found";
                    return null;
                }

                Elements daysAll = menuDiv.select("li[id~=menuid-\\d+$]");
                Elements menuAll = menuDiv.select("div[id~=menuid-\\d+-day]");

                List<String> dayNames = new ArrayList<>();
                String startDay = null, endDay = "";

                for (Element e: daysAll) {
                    String shortDay = e.text().substring(0, 3);

                    if (startDay == null) startDay = e.text().substring(4);
                    endDay = e.text().substring(4);

                    switch (shortDay) {
                        case "Mon": dayNames.add("monday");
                            break;
                        case "Tue": dayNames.add("tuesday");
                            break;
                        case "Wed": dayNames.add("wednesday");
                            break;
                        case "Thu": dayNames.add("thursday");
                            break;
                        case "Fri": dayNames.add("friday");
                            break;
                        case "Sat": dayNames.add("saturday");
                            break;
                        case "Sun": dayNames.add("sunday");
                            break;
                    }
                }

                StringBuilder currentMonth = new StringBuilder(), currentDay = new StringBuilder(), currentYear = new StringBuilder();
                CommonUtilities.loadCurrentDate(currentMonth, currentDay, currentYear);

                // "1/10/2019 - 1/21/2019"
                if (startDay != null && Integer.parseInt(startDay) < Integer.parseInt(endDay)) {
                    weekString = currentMonth.toString() + "/" + startDay + "/" + currentYear.toString() + " - " +
                            currentMonth.toString() + "/" + endDay + "/" + currentYear.toString();
                } else {
                    String nextMonth = String.valueOf(Integer.parseInt(currentMonth.toString()) + 1);
                    weekString = currentMonth.toString() + "/" + startDay + "/" + currentYear.toString() + " - " +
                            nextMonth + "/" + endDay + "/" + currentYear.toString();
                }

                // skipping year check
                currentMonth.delete(0, currentMonth.length());
                currentDay.delete(0, currentDay.length());
                currentYear.delete(0, currentYear.length());

                String stringToAppend;
                int index, i;

                for(i = 0; i < daysAll.size(); i++) {
                    Elements B = menuAll.get(i).select("div[class~=accordion-block breakfast]");
                    Elements L = menuAll.get(i).select("div[class~=accordion-block lunch]");
                    Elements D = menuAll.get(i).select("div[class~=accordion-block dinner]");
                    index = 1;

                    if (B.size() > 0) {
                        for (Element e : B.get(0).select("a[data-fooditemname]")) {
                            stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                            stringBuilderBreakfast.append(stringToAppend);
                            index++;
                        }
                    }
                    index = 1;
                    if (L.size() > 0) {
                        for (Element e : L.get(0).select("a[data-fooditemname]")) {
                            stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                            stringBuilderLunch.append(stringToAppend);
                            index++;
                        }
                    }
                    index = 1;
                    if(D.size() > 0) {
                        for (Element e : D.get(0).select("a[data-fooditemname]")) {
                            stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                            stringBuilderDinner.append(stringToAppend);
                            index++;
                        }
                    }

                    stringBuilderBreakfast.append((stringBuilderBreakfast.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                    stringBuilderLunch.append((stringBuilderLunch.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                    stringBuilderDinner.append((stringBuilderDinner.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");

                    if(updateDatabase) bingDiningMenu.diningDatabase.updateMenuItem(i+1,dayNames.get(i), stringBuilderBreakfast.toString(),
                            stringBuilderLunch.toString(), stringBuilderDinner.toString());
                    else bingDiningMenu.diningDatabase.insertMenuItem(dayNames.get(i), stringBuilderBreakfast.toString(),
                            stringBuilderLunch.toString(), stringBuilderDinner.toString());

                    stringBuilderBreakfast.delete(0, stringBuilderBreakfast.length());
                    stringBuilderLunch.delete(0, stringBuilderLunch.length());
                    stringBuilderDinner.delete(0, stringBuilderDinner.length());
                }
                dayNames.clear();
            } catch(IOException e){
                errorMessage = "Error occurred while processing data - most likely no data on server";
                e.printStackTrace();
                loadEmptyMenu = true;
            } catch (NumberFormatException e) {
                errorMessage = "Web data has changed on backend - dev will push an update soon";
                e.printStackTrace();
                loadEmptyMenu = true;
            } catch (Exception e) {
                errorMessage = "Either one or more dining halls closed, or unknown error";
                e.printStackTrace();
                loadEmptyMenu = true;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(pD != null) pD.dismiss();

            final BingDiningMenu bingDiningMenu = activityReference.get();

            if(loadEmptyMenu){
                Objects.requireNonNull(bingDiningMenu).diningMenuView.setBackground(ContextCompat.getDrawable(bingDiningMenu.context, R.drawable.cloud_2));

                if (bingDiningMenu.diningMenuView.isShown())
                    Toast.makeText(activityReference.get().context, errorMessage, Toast.LENGTH_SHORT).show();

                bingDiningMenu.diningDatabase.deleteAllItems();
                bingDiningMenu.saveBingWeekData(BingDiningMenu.FAILED_MENU_DATE);
            }else {
                Objects.requireNonNull(bingDiningMenu).saveBingWeekData(weekString);
                bingDiningMenu.loadSortedData();
            }

            if (bingDiningMenu.tabLayout.getTabAt(bingDiningMenu.tabLayout.getSelectedTabPosition()).
                    getText().equals(bingDiningMenu.title)) {
                bingDiningMenu.setView();
            }

            bingDiningMenu.diningDatabase.close();
        }
    }

    //check if a new menu is available by checking on the week date of the menu
    private static class NewMenuRequest extends AsyncTask<String, Void, Boolean> {
        private String weekString = "Not Sample Menu";
        WeakReference<BingDiningMenu> weakReference;

        NewMenuRequest(BingDiningMenu context) {
            weakReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(String ... strings) {
            final String url = strings[0];

            try {
                // check for valid connection
                Connection.Response response = Jsoup.connect(url).timeout(2000).execute();
                if (response.statusCode() != 200) {
                    return false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean updatePassed) {
            super.onPostExecute(updatePassed);
            BingDiningMenu bingDiningMenu = weakReference.get();

            if (updatePassed) {
                if (weekString.compareTo("Sample Menu") != 0) {
                    BingCrawler bingCrawler = new WebScrapper(bingDiningMenu);
                    bingCrawler.getDiningMenuData(true);
                } else {
                    Toast.makeText(bingDiningMenu.context, "No new menu yet", Toast.LENGTH_SHORT).show();
                    bingDiningMenu.setView();
                    bingDiningMenu.diningDatabase.close();
                }
            } else {
                Toast.makeText(bingDiningMenu.context, "No data found on server", Toast.LENGTH_SHORT).show();
                bingDiningMenu.diningMenuView.setBackground(ContextCompat.getDrawable(bingDiningMenu.context, R.drawable.cloud_2));
                bingDiningMenu.saveBingWeekData(BingDiningMenu.FAILED_MENU_DATE);
                bingDiningMenu.diningDatabase.close();
            }

        }
    }

}
