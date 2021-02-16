## Recommendation Service
`app.py` contains the code for Dyetr's recommendation service. Given a `user_id` and a `meal` (`"breakfast"`, `"lunch"`, or `"dinner"`), the service returns personalized food recommendations in JSON of the structure
```JSON
{
    recommendations: [
        {
            "calories": <float>,
            "carbohydrates": <float>,
            "fat": <float>,
            "id": <str: uuid>,
            "name": <str>,
            "protein": <float>
        },
        etc...
    ]
}
```

### Service Setup
Before running this service, ensure that `:SIMILAR` relationships have been formed between the food nodes in the Neo4j database. To do this, run the query in `../Neo4j/Similarity Query.txt` within the Neo4j desktop application. For each food node in the database, this query relates it to the three most similar foods, based on the amounts of calories, carbohydrates, fats, and protein. THE SERVICE WILL NOT WORK UNLESS THIS HAS BEEN DONE!!!