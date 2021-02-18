from flask import Flask, request
from neo4j import GraphDatabase

# enum
CONNECTION_STRING = 'bolt://localhost:7687'
USER_NAME = 'neo4j'
PASSWORD = 'password'


app = Flask(__name__)
db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))

@app.route('/api/v1/recommend', methods=['GET'])
def recommend():
    try:
        user_id = request.args.get('user_id')
        meal = request.args.get('meal')
        assert (meal in ["breakfast", "lunch", "dinner"])
    except KeyError as e:
        return ({"status": f"{str(e)}"}, 500)
    except AssertionError as e:
        return ({"error": '"meal" must be "breakfast", "lunch", or "dinner"'}, 500)

    # build up the Neo4j query
    query = (" ".join([
        f"MATCH (u:User {{id: '{user_id}'}})-[breakfast:ATE {{meal: '{meal}'}}]->(breakfastFood:food)",
        f"MATCH (u:User {{id: '{user_id}'}})-[lunch:ATE {{meal: '{meal}'}}]->(lunchFood:food)",
        f"MATCH (u:User {{id: '{user_id}'}})-[dinner:ATE {{meal: '{meal}'}}]->(dinnerFood:food)",
        "WHERE date(breakfast.time) = date(lunch.time) = date(dinner.time)",
        "AND breakfastFood.calories + lunchFood.calories + dinnerFood.calories < u.calorieGoal",
        f"MATCH ({meal}Food)-[similarity:SIMILAR]->(resultFood:food)",
        "WHERE similarity.score > 0",
        "RETURN collect(resultFood) AS recommendations"
    ]))

    
    with db_driver.session() as session:
        result = session.run(query, id=user_id, meal=meal)
        record = result.single()
        response = record.data()
    
    return (response, 200)

db_driver.close()

if __name__ == '__main__':
    app.run()
