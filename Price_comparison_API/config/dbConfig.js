const sql = require('mssql');

const dbConfig = {
    server: 'localhost',
    user: 'sa',
    password: 'reallyStrongPwd123',
    database: 'Price_Comparison',
    options: {
        encrypt: true,
        trustServerCertificate: true,
    },
    port: 1433,
};

const connectionPool = new sql.ConnectionPool(dbConfig)
    .connect()
    .then(pool => {
        console.log("connected")
        return pool;
    })


module.exports = connectionPool;