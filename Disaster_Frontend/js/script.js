document.addEventListener("DOMContentLoaded", function () {

    let reportForm = document.getElementById("reportForm");

    if (reportForm) {

        reportForm.addEventListener("submit", async function (e) {

            e.preventDefault();
           alert("Submit button clicked");  
            const disasterType = document.getElementById("disasterType").value;

            let location = document.getElementById("location").value;

            if (location === "Other") {
                location = document.getElementById("reportOtherLocation").value;
            }

            const description = document.getElementById("description").value;

            try {

                const response = await fetch("http://localhost:8080/report", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        disasterType,
                        location,
                        description
                    })
                });

                const msg = await response.text();

                alert(msg);

                reportForm.reset();

            } catch (error) {

                console.error(error);
                alert("Backend connection failed!");

            }

        });

        

    }

});
let helpForm = document.getElementById("helpForm");

if(helpForm){

    helpForm.addEventListener("submit", async function(e){

        e.preventDefault();

        const helpType = document.getElementById("helpType").value;

        let location = document.getElementById("helpLocation").value;

        if(location === "Other"){
            location = document.getElementById("helpOtherLocation").value;
        }

        const description = document.getElementById("helpDescription").value;

        try{

            const response = await fetch("http://localhost:8080/help",{
                method:"POST",
                headers:{
                    "Content-Type":"application/json"
                },
                body:JSON.stringify({
                    helpType,
                    location,
                    description
                })
            });

            const msg = await response.text();

            alert(msg);
            helpForm.reset();

        }catch(error){

            console.log(error);
            alert("Backend connection failed!");

        }

    });

}