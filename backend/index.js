const express = require('express')
const app = express()

app.use(express.json())

app.post("/create_app",(req,res)=>{
    
    const appname = req.body.appname;
    const weblink = req.body.applink;
    const appcolor = req.body.appcolor;
    const permission = req.body.permission;
    const webCache = req.body.webCache;
    const appicon = req.body.appicon;

    console.log("received create_app");

    console.log(appicon);

    res.status(200).send();
    // const Img_Path = //get the Path for the image stored using multer

})

app.listen(5000,()=>{
    console.log("Listening on port 5000....");
})