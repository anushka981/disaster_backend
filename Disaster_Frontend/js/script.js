// ===============================
// SHOW MESSAGE
// ===============================

function showAlert(icon, title, text){

    Swal.fire({
        icon,
        title,
        text
    });

}


// ===============================
// SEND REPORT WITH IMAGE
// ===============================

async function sendReport(formData, form){

    try {

        const response = await fetch(
            "http://localhost:8080/report",
            {
                method:"POST",
                headers:{
                    "Authorization": "Bearer " + localStorage.getItem("token")
                },
                body:formData
            }
        );


        const data = await response.json();


        if(response.ok){

            Swal.fire({

                icon:"success",
                title:"Success",
                text:"Report submitted successfully",
                timer:1500,
                showConfirmButton:false

            });


            form.reset();

        }
        else{

            showAlert(
                "error",
                "Failed",
                data.error || "Something went wrong"
            );

        }


    }
    catch(error){

        console.error(error);

        showAlert(
            "error",
            "Connection Failed",
            "Backend server is not reachable."
        );

    }

}




// ===============================
// SEND HELP DATA
// ===============================

async function sendHelp(url, data, form){

    try {

        const response = await fetch(
            `http://localhost:8080${url}`,
            {

                method:"POST",

                headers:{
                    "Content-Type":"application/json",
                    "Authorization": "Bearer " + localStorage.getItem("token")
                },

                body:JSON.stringify(data)

            }
        );


        const result = await response.json();


        if(response.ok){

            Swal.fire({

                icon:"success",
                title:"Success",
                text:"Help request submitted successfully",
                timer:1500,
                showConfirmButton:false

            });


            form.reset();

        }
        else{

            showAlert(
                "error",
                "Failed",
                result.error || "Something went wrong"
            );

        }

    }
    catch(error){

        showAlert(
            "error",
            "Connection Failed",
            "Backend server is not reachable."
        );

    }

}




// ===============================
// PAGE LOAD
// ===============================

document.addEventListener(
"DOMContentLoaded",
function(){



// ===============================
// REPORT FORM
// ===============================


const reportForm =
document.getElementById("reportForm");



if(reportForm){


reportForm.addEventListener(
"submit",
function(e){


e.preventDefault();



let location =
document.getElementById("location").value;



if(location==="Other"){

location =
document.getElementById(
"reportOtherLocation"
).value;

}



const formData = new FormData();



formData.append(
"disasterType",
document.getElementById("disasterType").value
);



formData.append(
"location",
location
);



formData.append(
"description",
document.getElementById("description").value
);



// image add

const image =
document.getElementById("image").files[0];


if(image){

    formData.append(
        "image",
        image
    );

}



// capture live GPS location before submitting

if(navigator.geolocation){

    navigator.geolocation.getCurrentPosition(

        function(position){

            formData.append("latitude", position.coords.latitude);
            formData.append("longitude", position.coords.longitude);

            sendReport(formData, reportForm);

        },

        function(error){

            console.warn("Location not available:", error.message);

            sendReport(formData, reportForm);

        },

        { timeout: 8000 }

    );

}
else{

    sendReport(formData, reportForm);

}



});

}



});