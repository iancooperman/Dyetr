from flask import Flask
from flask import request
from neo4j import GraphDatabase

# enum
CONNECTION_STRING = 'bolt://localhost:7687'
USER_NAME = 'neo4j'
PASSWORD = 'password'


app = Flask(__name__)


@app.route('/api/v1/recommend', methods=['GET'])
def recommend():
    try:
        user_id = request.args.get('user_id')
        meal = request.args.get('meal')
    except KeyError as e:
        return {"status": f"{e}"}, 500

    # build up the Neo4j query
    query = """MATCH (u:User {id:'{user_id}'})-[breakfast:ATE {meal:'breakfast'\}]->(breakfastFood:food)
            MATCH (u:User {id:'{user_id}'})-[lunch:ATE {meal:'lunch'}]->(lunchFood:food)
            MATCH (u:User {id:'{user_id}'})-[dinner:ATE {meal:'dinner'}]->(dinnerFood:food)
            WHERE date(breakfast.time) = date(lunch.time) = date(dinner.time) 
                AND breakfastFood.calories + lunchFood.calories + dinnerFood.calories < u.calorieGoal

            MATCH ({meal}Food)-[similarity:SIMILAR]->(resultFood:food)
            WHERE similarity.score > 0
            RETURN collect(resultFood)[0]""".format(user_id=user_id, meal=meal)


    db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))
    result = None
    with db_driver.session() as session:
        result = session.run(query)

    db_driver.close()
    return result, 200

