package net.abigailthompson.grocerylist;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileIO {
    public static final String TAG = "FileIO";

    public ArrayList<Grocery> ReadFromXMLFile(String filename,
                                           AppCompatActivity activity)
    {
        ArrayList<Grocery>  Grocerys = new ArrayList<Grocery>();
        Log.d(TAG, "ReadFromXMLFile: Start");
        try{

            InputStream is = activity.openFileInput(filename);
            XmlPullParser xmlPullParser = Xml.newPullParser();
            InputStreamReader isr = new InputStreamReader(is);
            xmlPullParser.setInput(isr);

            while(xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                if(xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                {
                    if(xmlPullParser.getName().equals("Grocery"))
                    {
                        int id = Integer.parseInt(xmlPullParser.getAttributeValue(null, "id"));
                        String firstName = xmlPullParser.getAttributeValue(null, "firstname");
                        String lastName = xmlPullParser.getAttributeValue(null, "lastname");
                        //Grocery Grocery = new Grocery(id, firstName, lastName);
                        //Grocerys.add(Grocery);
                        //Log.d(TAG, "ReadFromXMLFile: " + Grocery.toString());
                    }
                }
                xmlPullParser.next();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "ReadFromXMLFile: Error: " + e.getMessage());
        }
        Log.d(TAG, "ReadFromXMLFile: End");
        return Grocerys;
    }

    public void WriteXMLFile(String filename,
                             AppCompatActivity activity,
                             ArrayList<Grocery> Grocerys)
    {
        Log.d(TAG, "WriteXMLFile: Start: " + filename);
        XmlSerializer serializer = Xml.newSerializer();
        File file = new File(filename);
        Log.d(TAG, "WriteXMLFile: 2");
        OutputStreamWriter writer = null;
        try{

            //file.createNewFile();
            Log.d(TAG, "WriteXMLFile: 3");
            writer = new OutputStreamWriter(activity.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE));
            Log.d(TAG, "WriteXMLFile: 4");
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Grocerys");
            serializer.attribute("", "number", String.valueOf(Grocerys.size()));

            for(Grocery Grocery: Grocerys)
            {
                serializer.startTag("", "Grocery");
                serializer.attribute("", "id", String.valueOf(Grocery.getId()));
                //serializer.attribute("", "firstname", String.valueOf(Grocery.getFirstName()));
                //serializer.attribute("", "lastname", String.valueOf(Grocery.getLastName()));
                serializer.endTag("", "Grocery");
                Log.d(TAG, "WriteXMLFile: " + Grocery.toString());
            }
            serializer.endTag("", "Grocerys");
            serializer.endDocument();
            serializer.flush();
            writer.close();
            Log.d(TAG, "WriteXMLFile: " + Grocerys.size() + " Grocerys written.");

            Log.d(TAG, "WriteXMLFile: End");
        }
        catch(Exception e)
        {
            Log.d(TAG, "WriteXMLFile: " + e.getMessage());
        }

        Log.d(TAG, "WriteXMLFile: End");
    }

    public void writeFile(String filename,
                          AppCompatActivity activity,
                          String[] items)
    {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(activity.openFileOutput(filename, Context.MODE_PRIVATE));
            String line = "";

            for(Integer counter = 0; counter < items.length; counter++)
            {
                line = items[counter];
                if(counter < items.length-1)
                    line += "\r\n";
                writer.write(line);
                Log.d(TAG, "writeFile: " + line);
            }
            writer.close();
        }
        catch(FileNotFoundException e)
        {
            Log.d(TAG, "writeFile: FileNotFoundException:" + e.getMessage());
        }
        catch(IOException e)
        {
            Log.d(TAG, "writeFile: IOException:" + e.getMessage());
        }
        catch(Exception e) {
            Log.d(TAG, "writeFile: " + e.getMessage());
        }
    }

    public static ArrayList<String> readFile(String filename, AppCompatActivity activity)
    {
        ArrayList<String> items = new ArrayList<String>();

        try{
            InputStream is = activity.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                items.add(line);
            }
            is.close();
        }
        catch(Exception e)
        {
            Log.d(TAG, "readFile: " + e.getMessage());
        }

        return items;
    }
}
