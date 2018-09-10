package com.nickhe.reciperescue;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainLoginActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userPassword;
    private TextView screenInfo;
    private Button loginBtn;
    private int counter = 5;
    private TextView userRegView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPwView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_login);


        initializeViews();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        screenInfo.setText("No of attempts remaining: 5");
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!=null){
            finish();//meaning if there is no user then it will stay at the main activity and have to enter sign in details again.
            startActivity(new Intent(MainLoginActivity.this,SplashScreenActivity.class));
        }

        //providing onclick function for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                validate(userName.getText().toString(), userPassword.getText().toString());

            }

        });
        userRegView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainLoginActivity.this, RegisterToFirebaseActivity.class));
            }
        });

        //providing onclick function for forgot password text view
        forgotPwView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainLoginActivity.this, ForgotPasswordActivity.class));
            }
        });

    }

    /**
     * This method will initialize all the variables and upon its call,
     * it will show all the views to the screen
     */
    private void initializeViews(){
        userName = (EditText) findViewById(R.id.userNameField);

        userPassword = (EditText) findViewById(R.id.userPasswordField);

        screenInfo = (TextView) findViewById(R.id.attemptsLoginInfo);

        loginBtn = (Button) findViewById(R.id.loginButton);
        userRegView = (TextView) findViewById(R.id.RegTextView);
        forgotPwView= (TextView) findViewById(R.id.forgotPwTV);

    }


    /**
     * This method will validate if the given name and the password are valid and if they are valid then it will let
     * user to enter to new window otherwise it will send error message
     * @param userName
     * @param userPassword
     */

    private void validate(String userName, String userPassword) {
        progressDialog.setMessage("Logging in ");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();

                    checkEmailVerification();
                }
                else{
                    progressDialog.cancel();
                    Toast.makeText(MainLoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                    counter--;
                    screenInfo.setText("No of attempts remaining: "+ counter);
                    if(counter==0){
                        loginBtn.setEnabled(false);
                    }
                }
            }
        });
    }

    /**
     * This method will check the email provided by the user during loging in and if the email
     * is correct then it will let user enter to new window otherwise it will sign out from the firebase
     * and ask user to provide correct email address.
     */
    private void checkEmailVerification(){
        FirebaseUser firebaseUser= firebaseAuth.getInstance().getCurrentUser();
        Boolean flag= firebaseUser.isEmailVerified();
        startActivity(new Intent(MainLoginActivity.this, SplashScreenActivity.class));

//        //if email is verified then link this to the second activity
//        if(flag){
//            finish();//finishes this main activity and directs it to the second activity.
//            startActivity(new Intent(MainLoginActivity.this, SplashScreenActivity.class));
//        }else{//if the email is not verified then send a toast message to user and sign out from the firebase
//            Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();
//            //Need to sign out until user provides the valid email address.
//            firebaseAuth.signOut();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

}