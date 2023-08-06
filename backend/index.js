const express = require('express')
const app = express()

app.use(express.json())

app.post("/home",(req,res)=>{
    
    const appname = req.body.appname;
    const weblink = req.body.weblink;
    // const Img_Path = //get the Path for the image stored using multer

})

app.listen(5000,()=>{
    console.log("Listening on port 5000....");
})