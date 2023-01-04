// element variable
const admin_btn = document.getElementsByClassName('admin-btn');
const user_btn = document.getElementsByClassName('user-btn');

// variable
let getAuth = "USER";

function SetAuthToAdminBtn(){
    getAuth = "ADMIN";
    console.log("auth state => ", getAuth);
}

function SetAuthToUserBtn(){
    getAuth = "USER";
    console.log("auth state => ", getAuth);
}