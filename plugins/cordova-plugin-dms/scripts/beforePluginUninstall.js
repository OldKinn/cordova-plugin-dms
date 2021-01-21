const path = require('path');
const fs = require('fs-extra');
const figlet = require('figlet');

const outputChar = (str) => {
    return new Promise((resolve) => {
        figlet(str, function(err, data) {
            if (err) {
                return resolve(str);
            }
            return resolve(data);
        });
    });
};

module.exports = async function(ctx) {
    const targetLibs = path.join(ctx.opts.projectRoot, './platforms/android/app/libs');
    await fs.remove(targetLibs);
    const targetLicense = path.join(ctx.opts.projectRoot, './platforms/android/app/src/main/jniLibs');
    await fs.remove(targetLicense);
    const chars = await outputChar('REMOVE SUCCESS');
    console.log(chars);
    console.log('^_^ remove londen plugin SDK');
};