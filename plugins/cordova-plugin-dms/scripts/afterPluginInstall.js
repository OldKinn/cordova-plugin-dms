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
    const sourceLibs = path.join(ctx.opts.plugin.dir, './src/libs');
    const targetLibs = path.join(ctx.opts.projectRoot, './platforms/android/app/libs');
    await fs.copy(sourceLibs, targetLibs);
    const sourceJNI = path.join(ctx.opts.plugin.dir, './src/jniLibs');
    const targetJNI = path.join(ctx.opts.projectRoot, './platforms/android/app/src/main/jniLibs');
    await fs.copy(sourceJNI, targetJNI);
    const chars = await outputChar('ADD SUCCESS');
    console.log(chars);
    console.log('^_^ dms plugin add success!');
};
