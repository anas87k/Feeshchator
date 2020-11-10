// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();
var options = {
    priority: 'high'
};
var topic = '/topics/feeshchator';
var pesanph, pesansuhu, pesansalinitas;
exports.PHmonitor = functions.database.ref('/monitoring/PH')
    .onUpdate((snapshot, context) => {
        //const flag = snapshot.before.val();   TO GET THE OLD VALUE BEFORE UPDATE
        const ph = snapshot.after.val();
        if (parseFloat(ph) > 8.5)
            pesanph = 'PH Air Saat ini '+ph+', PH Diatas Batas Normal yaitu 8.5. Untuk Menstabilkan Dapat Melihat Informasi Panduan';
        else if (parseFloat(ph) < 7)
            pesanph = 'PH Air Saat ini '+ph+', PH Dibawah Batas Normal yaitu 7. Untuk Menstabilkan Dapat Melihat Informasi Panduan';
        var phpayload = {
            notification: {
                title: 'Peringatan',
                body: pesanph,
                sound: 'default'
            },
            data: {
                judul: 'Peringatan',
                isi: pesanph,
                sound: 'default'
            }
        };

        if (parseFloat(ph) > 8.5 || parseFloat(ph) < 7) {
            admin.messaging().sendToTopic(topic, phpayload, options).then((response) => {
                console.log("Message sent successfully:", response);
                return response;
            }).catch((error) => {
                console.log("Error sending message: ", error);
            });
        }
    });

exports.Suhumonitor = functions.database.ref('/monitoring/Temperature')
     .onUpdate((snapshot, context) => {
         //const flag = snapshot.before.val();   TO GET THE OLD VALUE BEFORE UPDATE
         const suhu = snapshot.after.val();
         if (parseFloat(suhu) > 35)
             pesansuhu = 'Suhu Air Saat ini '+suhu+', Suhu Diatas Batas Normal yaitu 35';
         else if (parseFloat(suhu) < 27)
             pesansuhu = 'Suhu Air Saat ini '+suhu+', Suhu Dibawah Batas Normal yaitu 27'
         var suhupayload = {
             notification: {
                 title: 'Peringatan',
                 body: pesansuhu,
                 sound: 'default'
             },
             data: {
                 judul: 'Peringatan',
                 isi: pesansuhu,
                 sound: 'default'
             }
         };

         if (parseFloat(suhu) > 35 || parseFloat(suhu) < 27) {
             admin.messaging().sendToTopic(topic, suhupayload, options).then((response) => {
                 console.log("Message sent successfully:", response);
                 return response;
             }).catch((error) => {
                 console.log("Error sending message: ", error);
             });
         }
     });

exports.SalinitasMonitor = functions.database.ref('/monitoring/Salinitas')
     .onUpdate((snapshot, context) => {
         //const flag = snapshot.before.val();   TO GET THE OLD VALUE BEFORE UPDATE
         const salinitas = snapshot.after.val();
         if (parseFloat(salinitas) > 25)
             pesansalinitas = 'Salinitas Air Saat ini '+salinitas+', Salinitas Diatas Batas Normal yaitu 25. Untuk Menstabilkan Salinitas Dapat Melihat Informasi Panduan';
         else if (parseFloat(salinitas) < 5)
             pesansalinitas = 'Salinitas Air Saat ini '+salinitas+', Salinitas Dibawah Batas Normal yaitu 5. Untuk Menstabilkan Salinitas Dapat Melihat Informasi Panduan';
         var salinitaspayload = {
             notification: {
                 title: 'Peringatan',
                 body: pesansalinitas,
                 sound: 'default'
             },
             data: {
                 judul: 'Peringatan',
                 isi: pesansalinitas,
                 sound: 'default'
             }
         };

         if (parseFloat(salinitas) > 25 || parseFloat(salinitas) < 5) {
             admin.messaging().sendToTopic(topic, salinitaspayload, options).then((response) => {
                 console.log("Message sent successfully:", response);
                 return response;
             }).catch((error) => {
                 console.log("Error sending message: ", error);
             });
         }
     });