# djangoapp

This app is used to track the student in universtiy.

Students need to login the app by their stuent id and password to get the QRcode which is a permission to enter the gate.

The entire process is below:
1. First, student should regist an account. Press the resgist button and then enter their personal inforamtion.
2. Second, take a selfie to upload their face image.
3. Third, verify their account by entering the verifying code, verifying code should be sent in their registed email.
4. Finally, once they enter the gate, they should login their account.

if login is success, then app will create a QRcode. Student should let this QRcode scanned by scanner to check their identity.
After scanning, there is a camera at the gate taking a picture of the student to confirm that the person who log in to the account is the same as the person who registered.
Also, the camera will check whether the student wears a mask.


