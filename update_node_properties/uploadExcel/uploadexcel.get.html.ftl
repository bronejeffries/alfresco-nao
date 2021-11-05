<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk"
        crossorigin="anonymous">
    <title>Upload excel</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.16.2/xlsx.full.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>

<body>
    <div class="conatiner mt-5">
        <div class=row>
            <div class="col-md-5">
                <div class="col-md-12">
                    <label>Username</label>
                    <input type="text" id="username_input" />
                </div>
                <div class="col-md-12">
                    <label>password</label>
                    <input type="password" id="password_input" />
                </div>
            </div>
            
        </div>
        <div class="row">
            <div class="col-md-4">
                <input class="form-control" type="file" id="input" accept=".xls,.xlsx"  >
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary" id="button">upload</button>
            </div>
            <div class="col-md-12">
               <p>
                upload status (<span id="upload_status"></span>)
               </p>
               <p>
                upload count (<span id="upload_count"></span>)
               </p>
               <p>
                upload failed at <strong>uploads may fail because of the illegal characters (i.e './\') in the subject</strong><br>*<span id="upload_failed"></span>*
               </p>
            </div>
            
            <div class="col-md-12">
                <p>UPLOADED</p>
                <pre id="jsondata"></pre>
            </div>
        </div>
    </div>
</body>
<script>

    let selectedFile;
    
    let update_node_url = window.location.origin+"/alfresco/api/-default-/public/alfresco/versions/1/nodes/"

    document.getElementById('input').addEventListener("change", (event) => {

        selectedFile = event.target.files[0];
    
    })

    let data=[{
        "name":"jayanth",
        "data":"scd",
        "abc":"sdef"
    }]


    document.getElementById('button').addEventListener("click", () => {
       
        let username_provided = $("#username_input").val()
        let password_provided = $("#password_input").val()
        let user_data = {'userId':username_provided,'password':password_provided}
        user_data = JSON.stringify(user_data)
        ticket_url = window.location.origin+"/alfresco/api/-default-/public/authentication/versions/1/tickets"

        $.ajax({
                url:ticket_url,
                type:"POST",
                data:user_data,
                dataType:'text',
                contentType:'application/json',
                success:function(result){
                    result = JSON.parse(result)
                    console.log(result)

                    if(result.entry){
                        let alf_ticket = result.entry.id

                        if(selectedFile){
                            let fileReader = new FileReader();
                            fileReader.readAsBinaryString(selectedFile);
                            fileReader.onload = (event)=>{
                                let data = event.target.result;
                                //  console.log("data",data);
                                let workbook = XLSX.read(data,{type:"binary"});
                                console.log("workbooks",workbook.SheetNames);
                                var upload_count = 0
                                var error_message = ""
                                var upload_status = document.getElementById("upload_status")
                                workbook.SheetNames.forEach(sheet => {
                                    let rowObject = XLSX.utils.sheet_to_row_object_array(workbook.Sheets[sheet]);
                                    console.log(rowObject);
                                    upload_status.innerHTML = "Uploading "+sheet+"..."
                                    var total_upload = rowObject.length
                                    //try{
                                        Array.from(rowObject).forEach(row=>{
                                        
                                            let node_id = row['nodeID']
                                            console.log(node_id)
                                            if(typeof node_id!="undefined"){
                                                
                                                let update_url = update_node_url+node_id+"?alf_ticket="+alf_ticket
                                                delete row['nodeID']

                                                node_update_data = {'properties':row}
                                                console.log(node_update_data)
                                                node_update_data = JSON.stringify(node_update_data)

                                                $.ajax({
                                                    url:update_url,
                                                    type:"PUT",
                                                    data:node_update_data,
                                                    dataType:'text',
                                                    contentType:'application/json',
                                                    success:function(result){
                                                        upload_count += 1
                                                        total_upload -= 1
                                                        document.getElementById("upload_count").innerHTML = upload_count
                                                        console.log(JSON.parse(result));
                                                        if(total_upload==0){
                                                            upload_status.innerHTML="done"
                                                        }
                                                    },
                                                    error:function(error){
                                                        console.log(error);
                                                        total_upload -= 1
                                                        var error_par = document.getElementById("upload_failed")
                                                        var error_innerHtml = error_par.innerHTML
                                                        error_innerHtml += folder_data+"<br>"
                                                        error_par.innerHTML = error_innerHtml
                                                        if(total_upload==0){
                                                            upload_status.innerHTML="done"
                                                        }
                                                    }
                                                })

                                            }
                                            
                                        })
                                    
                                        document.getElementById("jsondata").innerHTML = document.getElementById("jsondata").innerHTML + JSON.stringify(rowObject,undefined,4)
                                    //}catch(params){

                                     //   upload_status.innerHTML = "error"
                                    
                                    //}
                                    

                                });

                                document.getElementById("jsondata").innerHTML = document.getElementById("jsondata").innerHTML + "<a href='"+window.location.href+"'>Refresh page</a>"
                            }
                        }

                    }
                    
                },
                error:function(error){
                    console.log(error);
                    alert("Authentication failed")
                }
            })
    });

</script>

</html>