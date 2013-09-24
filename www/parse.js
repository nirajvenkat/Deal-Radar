var categoryGlob = null;

function submitForm(bssid, category, company, dealTitle) {
  
  Parse.initialize("XIyae4PpFDVrRGo0P7inT7j9WGxVUeNqHEMtwQGl", "hxjNjdr4bangevzHgiLTYlXptcUMflGdIQdtaret");

  var fileUploadControl = $("#photoFileUpload")[0];
  if (fileUploadControl.files.length > 0) {
    var file = fileUploadControl.files[0];
    var name = "photo.jpg";
    var parseFile = new Parse.File(name, file);
    
    parseFile.save().then(function() {
      // The file has been saved to Parse.
    }, function(error) {
      // The file either could not be read, or could not be saved to Parse.
    });
  
    var Routers = Parse.Object.extend("Routers");
    var router = new Routers();
  
  router.set("BSSID", bssid);
  router.set("Category", categoryGlob);
  router.set("Company", company);
  router.set("Deal_Image", parseFile);
  router.set("Deal_Title", dealTitle);
  router.set("Exp_Date", new Date());
    router.save(null, {
       success: function(router) {
      // The object was saved successfully.
      $('#bssid').val("");
      $('#company').val("");
      $('#dealTitle').val("");
      $('#submitAlert').html('<div class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button><i class=\"icon-ok-sign\"></i><strong> Successfully pushed promotion!</strong></div>');
     },
       error: function(routers, error) {
         // The save failed.
         // error is a Parse.Error with an error code and description.
         $('#submitAlert').html('<div class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button><i class=\"icon-exclamation-sign\"></i><strong> Error pushing promotion!</string></div>');
      }
    });
  }
}

function setCategory(category) {
    $('#categoryDropdown').html(category);
    categoryGlob = category;
}