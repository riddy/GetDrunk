package org.drink.getdrunk.frontend;

import org.drink.getdrunk.R;

import android.app.ProgressDialog;
import android.content.Context;

class ProgressDialogBuilder {

   /**
    * Create a new default {@link ProgressDialog}.
    *
    * @param context required for creation
    * @return progressDialog which can be displayed using {@link ProgressDialog#show()} and hide by
    *         using {@link ProgressDialog#dismiss()};
    */
   static ProgressDialog build( Context context ) {
      ProgressDialog progressDialog = new ProgressDialog( context, R.style.Dialog_Progress );
      progressDialog.setCancelable( false );
      return progressDialog;
   }
}
