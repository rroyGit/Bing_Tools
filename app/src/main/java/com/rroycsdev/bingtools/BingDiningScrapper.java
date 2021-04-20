package com.rroycsdev.bingtools;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;

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

public class BingDiningScrapper implements WebCrawler {

    private BingDiningMenu bingDiningMenu;


    BingDiningScrapper(BingDiningMenu bingDiningMenu) {
        this.bingDiningMenu = bingDiningMenu;
    }

    @Override
    public DiningDataScrapper getDiningMenuData(boolean isInsertDatabase) {
        DiningDataScrapper diningDataScrapper = new DiningDataScrapper(bingDiningMenu);
        diningDataScrapper.execute(isInsertDatabase);
        return diningDataScrapper;
    }

    @Override
    public void makeDailyRequest(String link) {
        new NewMenuRequest(bingDiningMenu).execute(link);
    }

    //make async web request using JSOUP
    //first parameter (Boolean), if true update database - if false insert into database
    static class DiningDataScrapper extends AsyncTask<Boolean, Void, Void> {
        private StringBuilder stringBuilderBreakfast = new StringBuilder();
        private StringBuilder stringBuilderLunch = new StringBuilder();
        private StringBuilder stringBuilderAfternoonSnack = new StringBuilder();
        private StringBuilder stringBuilderDinner = new StringBuilder();

        private String weekString;
        private Boolean isInsertDatabase = false;
        private Boolean loadEmptyMenu = false;
        private String errorMessage = "";
        ProgressDialog pD;

        //weak reference
        private WeakReference<BingDiningMenu> activityReference;

        DiningDataScrapper(BingDiningMenu bingDiningMenu){
            activityReference = new WeakReference<>(bingDiningMenu);
            pD = new ProgressDialog(bingDiningMenu.context);
            pD.setCancelable(false);
            pD.setMessage("Loading " + bingDiningMenu.title + " menu, please wait.");
        }

        @Override
        protected void onPreExecute() {
            BingDiningMenu bingDiningMenu = activityReference.get();
            bingDiningMenu.showSavedMsg = false;

            if (bingDiningMenu.isShowProgressDialog && bingDiningMenu.diningMenuView.getWindowToken() != null &&
                    Objects.equals(Objects.requireNonNull(bingDiningMenu.tabLayout.getTabAt(bingDiningMenu.tabLayout.getSelectedTabPosition())).
                            getText(), bingDiningMenu.title)) {
                pD.show();
            }
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            final BingDiningMenu bingDiningMenu = activityReference.get();
            if (params.length > 0) isInsertDatabase = params[0];

            if (bingDiningMenu == null) {
                loadEmptyMenu = true;
                return null;
            }

            if (isInsertDatabase) bingDiningMenu.diningDatabase.deleteAllItems();

            try {
                // test for valid connection
                Connection.Response response = Jsoup.connect(bingDiningMenu.link).timeout(5000).execute();
                if (response.statusCode() != 200) {
                    loadEmptyMenu = true;
                    return null;
                }

                Document doc2 = Jsoup.connect(bingDiningMenu.link).get();
                Element body = doc2.body();
                if (body == null) {
                    loadEmptyMenu = true;
                    errorMessage = "One or more dining halls are closed, no menu found";
                    return null;
                }

                Element menuDiv = body.getElementById("bite-menu");

                if (menuDiv != null) {
                    String menuActive = menuDiv.getElementById("bite-calc").select("p").text();
                    if (menuActive.equals("Sorry, no menu found") || menuActive.contains("not found")
                            || menuActive.contains("no menu")) {
                        loadEmptyMenu = true;
                        errorMessage = "One or more dining halls are closed, no menu found";
                        return null;
                    }
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
                    Elements B = menuAll.get(i).select("div[class~=breakfast]");
                    Elements L = menuAll.get(i).select("div[class~=lunch]");
                    Elements AS = menuAll.get(i).select("div[class~=afternoon]");
                    Elements D = menuAll.get(i).select("div[class~=dinner]");

                    index = 1;

                    if (B.size() > 0) {
                        for (Element e : B.get(0).select("a[data-fooditemname]")) {
                            stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                            stringBuilderBreakfast.append(stringToAppend);
                            index++;
                        }
                        for (int j = index; j > 1 && j <= 4; j++) stringBuilderBreakfast.append("\n");
                    }
                    index = 1;
                    if (L.size() > 0) {
                        for (Element e : L.get(0).select("a[data-fooditemname]")) {
                            stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                            stringBuilderLunch.append(stringToAppend);
                            index++;
                        }
                        for (int j = index; j > 1 && j <= 4; j++) stringBuilderLunch.append("\n");
                    }
                    index = 1;
                    if(AS.size() > 0) {
                        for (Element e : AS.get(0).select("a[data-fooditemname]")) {
                            stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                            stringBuilderAfternoonSnack.append(stringToAppend);
                            index++;
                        }
                        for (int j = index; j > 1 && j <= 4; j++) stringBuilderDinner.append("\n");
                    }
                    index = 1;
                    if(D.size() > 0) {
                        for (Element e : D.get(0).select("a[data-fooditemname]")) {
                            stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                            stringBuilderDinner.append(stringToAppend);
                            index++;
                        }
                        for (int j = index; j > 1 && j <= 4; j++) stringBuilderDinner.append("\n");
                    }

                    stringBuilderBreakfast.append((stringBuilderBreakfast.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                    stringBuilderLunch.append((stringBuilderLunch.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                    stringBuilderAfternoonSnack.append((stringBuilderDinner.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                    stringBuilderDinner.append((stringBuilderDinner.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");

                    if (isInsertDatabase) {
                        bingDiningMenu.diningDatabase.insertMenuItem(dayNames.get(i), stringBuilderBreakfast.toString(),
                                stringBuilderLunch.toString(), stringBuilderAfternoonSnack.toString(), stringBuilderDinner.toString());
                    } else {
                        bingDiningMenu.diningDatabase.updateMenuItem(i + 1, dayNames.get(i), stringBuilderBreakfast.toString(),
                                stringBuilderLunch.toString(), stringBuilderAfternoonSnack.toString(), stringBuilderDinner.toString());
                    }


                    stringBuilderBreakfast.delete(0, stringBuilderBreakfast.length());
                    stringBuilderLunch.delete(0, stringBuilderLunch.length());
                    stringBuilderAfternoonSnack.delete(0, stringBuilderAfternoonSnack.length());
                    stringBuilderDinner.delete(0, stringBuilderDinner.length());
                }

                dayNames.clear();
                bingDiningMenu.loadSortedData();
                Objects.requireNonNull(bingDiningMenu).saveMenuWeekDate(weekString);

            } catch (IOException e) {
                errorMessage = "Error occurred while processing data - most likely no data on server";
                e.printStackTrace();
                loadEmptyMenu = true;
            } catch (NumberFormatException e) {
                errorMessage = "Web data has changed on backend - dev will push an update soon";
                e.printStackTrace();
                loadEmptyMenu = true;
            } catch (Exception e) {
                errorMessage = "Dining hall closed, or unknown error";
                e.printStackTrace();
                loadEmptyMenu = true;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pD != null && pD.isShowing()) pD.dismiss();
            final BingDiningMenu bingDiningMenu = activityReference.get();
            bingDiningMenu.saveMenuMsg(errorMessage);
            bingDiningMenu.isShowProgressDialog = false;

            if (loadEmptyMenu) {
                bingDiningMenu.showSavedMsg = true;
                Objects.requireNonNull(bingDiningMenu).diningMenuView.setBackground(ContextCompat.getDrawable(bingDiningMenu.context, R.drawable.cloud_2));
                if (Objects.equals(Objects.requireNonNull(bingDiningMenu.tabLayout.getTabAt(bingDiningMenu.tabLayout.getSelectedTabPosition())).
                        getText(), bingDiningMenu.title) && bingDiningMenu.diningMenuView.isSelected() && !errorMessage.isEmpty())
                    Toast.makeText(activityReference.get().context, errorMessage, Toast.LENGTH_SHORT).show();

                bingDiningMenu.saveMenuWeekDate(BingDiningMenu.FAILED_MENU_DATE);
                bingDiningMenu.diningDatabase.deleteAllItems();
                bingDiningMenu.clearView();
            }

            if (Objects.equals(Objects.requireNonNull(bingDiningMenu.tabLayout.getTabAt(bingDiningMenu.tabLayout.getSelectedTabPosition())).
                    getText(), bingDiningMenu.title)) {
                bingDiningMenu.setView(true);
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
                    WebCrawler webCrawler = new BingDiningScrapper(bingDiningMenu);
                    webCrawler.getDiningMenuData(true);
                } else {
                    Toast.makeText(bingDiningMenu.context, "No new menu yet", Toast.LENGTH_SHORT).show();
                    bingDiningMenu.setView(true);
                    bingDiningMenu.diningDatabase.close();
                }
            } else {
                Toast.makeText(bingDiningMenu.context, "No data found on server", Toast.LENGTH_SHORT).show();
                bingDiningMenu.diningMenuView.setBackground(ContextCompat.getDrawable(bingDiningMenu.context, R.drawable.cloud_2));
                bingDiningMenu.saveMenuWeekDate(BingDiningMenu.FAILED_MENU_DATE);
                bingDiningMenu.diningDatabase.close();
            }
        }
    }
}
