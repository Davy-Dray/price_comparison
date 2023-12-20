const express = require('express');
const router = express.Router();
const connectionPool = require('../config/dbConfig')
const sql = require('mssql')


router.get('/search', async (req, res) => {
    const queryTerm = req.query.q || '';
    const numItems = parseInt(req.query.numitems, 10) || 10;
    const offset = parseInt(req.query.offset, 10) || 0;

    try {
        const pool = await connectionPool;

        const result = await pool.request().query(`

      SELECT p.*, pm.*
      FROM phone p
      INNER JOIN phoneModel pm ON p.phoneModelId = pm.id
      WHERE pm.brand LIKE '%${queryTerm}%'
      ORDER BY p.id
      OFFSET ${offset * numItems} ROWS
      FETCH NEXT ${numItems} ROWS ONLY
      
    `);

        res.status(200).json({
            message: 'Query successful',
            data: result.recordset,
        });

        await pool.close();
    } catch (error) {
        console.error(error.message);
        res.status(500).json({
            message: 'Error executing the query',
            error: error.message,
        });
    }
});

router.get('/products/:id', async function (req, res, next) {
    const id = req.params.id;

    try {
        const pool = await connectionPool;

        // Retrieve details of the specified product ID
        const productInfo = await pool.request()
            .input('id', sql.Int, id)
            .query('SELECT m.Brand, m.Description, m.ImageURL, p.Color, p.Capacity FROM PhoneModel m ' +
                'JOIN Phone p ON m.id = p.PhoneModel_id ' +
                'WHERE m.id = @id');

        // If productInfo is not empty, use its details to find similar products
        if (productInfo.recordset.length > 0) {
            const { Brand, Description, ImageURL, Color, Capacity } = productInfo.recordset[0];

            // Query to retrieve similar products based on brand
            const result = await pool.request()
                .input('brand', sql.NVarChar, Brand)
                .input('id', sql.Int, id)
                .query('SELECT m.Brand, m.Description, m.ImageURL, p.Color, p.Capacity FROM PhoneModel m ' +
                    'JOIN Phone p ON m.id = p.PhoneModel_id ' +
                    'WHERE m.Brand = @brand AND m.id <> @id');

            res.json({
                total: result.recordset.length,
                data: result.recordset
            });
        } else {
            // No product found with the specified ID
            res.status(404).send('Product not found');
        }
    } catch (err) {
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
});

router.get('/category/:id', async (req, res) => {
    const phoneId = req.params.id;

    try {
        const pool = await sql.connect(config);

        const result = await pool
            .request()
            .query(`SELECT * FROM category WHERE phone_id = ${phoneId}`);

        res.status(200).json({
            message: 'Query successful',
            data: result.recordset,
        });

        await pool.close();
    } catch (error) {
        console.error(error.message);
        res.status(500).json({
            message: 'Error executing the query',
            error: error.message,
        });
    }
});

module.exports = router;



