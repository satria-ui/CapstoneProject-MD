// Generated by view binder compiler. Do not edit!
package com.example.emochat.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.emochat.R;
import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityRegisterBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final EditText addImage;

  @NonNull
  public final MaterialButton buttonRegister;

  @NonNull
  public final RoundedImageView imageProfile;

  @NonNull
  public final EditText inputConfirmPassword;

  @NonNull
  public final EditText inputEmail;

  @NonNull
  public final EditText inputName;

  @NonNull
  public final EditText inputPassword;

  @NonNull
  public final FrameLayout layoutImage;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final TextView textAddImage;

  @NonNull
  public final TextView textLogin;

  private ActivityRegisterBinding(@NonNull ScrollView rootView, @NonNull EditText addImage,
      @NonNull MaterialButton buttonRegister, @NonNull RoundedImageView imageProfile,
      @NonNull EditText inputConfirmPassword, @NonNull EditText inputEmail,
      @NonNull EditText inputName, @NonNull EditText inputPassword,
      @NonNull FrameLayout layoutImage, @NonNull ProgressBar progressBar,
      @NonNull TextView textAddImage, @NonNull TextView textLogin) {
    this.rootView = rootView;
    this.addImage = addImage;
    this.buttonRegister = buttonRegister;
    this.imageProfile = imageProfile;
    this.inputConfirmPassword = inputConfirmPassword;
    this.inputEmail = inputEmail;
    this.inputName = inputName;
    this.inputPassword = inputPassword;
    this.layoutImage = layoutImage;
    this.progressBar = progressBar;
    this.textAddImage = textAddImage;
    this.textLogin = textLogin;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_register, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRegisterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.addImage;
      EditText addImage = ViewBindings.findChildViewById(rootView, id);
      if (addImage == null) {
        break missingId;
      }

      id = R.id.buttonRegister;
      MaterialButton buttonRegister = ViewBindings.findChildViewById(rootView, id);
      if (buttonRegister == null) {
        break missingId;
      }

      id = R.id.imageProfile;
      RoundedImageView imageProfile = ViewBindings.findChildViewById(rootView, id);
      if (imageProfile == null) {
        break missingId;
      }

      id = R.id.inputConfirmPassword;
      EditText inputConfirmPassword = ViewBindings.findChildViewById(rootView, id);
      if (inputConfirmPassword == null) {
        break missingId;
      }

      id = R.id.inputEmail;
      EditText inputEmail = ViewBindings.findChildViewById(rootView, id);
      if (inputEmail == null) {
        break missingId;
      }

      id = R.id.inputName;
      EditText inputName = ViewBindings.findChildViewById(rootView, id);
      if (inputName == null) {
        break missingId;
      }

      id = R.id.inputPassword;
      EditText inputPassword = ViewBindings.findChildViewById(rootView, id);
      if (inputPassword == null) {
        break missingId;
      }

      id = R.id.layoutImage;
      FrameLayout layoutImage = ViewBindings.findChildViewById(rootView, id);
      if (layoutImage == null) {
        break missingId;
      }

      id = R.id.progressBar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.textAddImage;
      TextView textAddImage = ViewBindings.findChildViewById(rootView, id);
      if (textAddImage == null) {
        break missingId;
      }

      id = R.id.textLogin;
      TextView textLogin = ViewBindings.findChildViewById(rootView, id);
      if (textLogin == null) {
        break missingId;
      }

      return new ActivityRegisterBinding((ScrollView) rootView, addImage, buttonRegister,
          imageProfile, inputConfirmPassword, inputEmail, inputName, inputPassword, layoutImage,
          progressBar, textAddImage, textLogin);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
