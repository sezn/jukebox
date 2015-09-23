package com.szn.jukebox.utils;

import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;
import android.widget.Toast;

import java.util.ArrayList;

import fr.infogene.contacto.R;
import fr.infogene.contacto.model.Store;


/**
 * Created by Julien Sezn on 19/08/2015.
 *
 */
public class ContactsUtility {



    public static void addContactNew(Context context, Store optician) {

        String accountname = null;
        String accounttype = null;

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accountname)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accounttype).build());

		/* Display Name Adding */
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        optician.getManagers().get(0)
                ).build());
		/* Mobile Number Adding */
        if (optician.getCoordinates().getPhone() != null && optician.getCoordinates().getPhone().trim().length() > 0) {
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                            ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                            optician.getCoordinates().getPhone())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

		/* Email ID Adding */
        if (optician.getCoordinates().getEmail() != null && optician.getCoordinates().getEmail().trim().length() > 0) {
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                            ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA,
                            optician.getCoordinates().getEmail())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }


        /** Address **/
        if (optician.getAddress() != null) {

            String line1 = "", postCode = "", city = "";

            if(optician.getAddress().getLine1() != null)
                line1 = optician.getAddress().getLine1();

            if(optician.getAddress().getCity() != null)
                city = optician.getAddress().getCity();

            if(optician.getAddress().getPostcode()!= null)
                postCode = optician.getAddress().getPostcode();

            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                            ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                            city)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                            postCode)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                            line1)
                    .build());
        }


        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.DATA1,
                        context.getString(R.string.app_name))
                .build());



        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(context.getApplicationContext(), "Contact ajouté!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static boolean contactExistsByNumber(Context context, String mobileNumber) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mobileNumber));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }



    public static void callNumber(Context con, String phone) {
        String url = "tel:" + phone;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        con.startActivity(intent);
    }


    public static void sendMail(Context con, String dest, String message, String subject)  {
        Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
//        sendMailIntent.setType("*/*");
        if(dest != null && !dest.isEmpty()) {
//            sendMailIntent = new Intent(Intent.ACTION_SENDTO);
            sendMailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{dest});
        }
        sendMailIntent.setType("text/plain");
        if(subject != null && !subject.isEmpty())
            sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendMailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message));
        try {
            con.startActivity(Intent.createChooser(sendMailIntent, con.getString(R.string.sendMail)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(con, con.getString(R.string.no_mail_client), Toast.LENGTH_SHORT).show();
        }
    }
}
