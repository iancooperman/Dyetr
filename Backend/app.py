from flask import Flask, request
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
        return ({"status": f"{e}"}, 500)

    # build up the Neo4j query
    query = (" ".join([
        "MATCH (u:User {id: $id})-[breakfast:ATE {meal: 'breakfast'}]->(b:food)",
        "MATCH (u:User {id: $id})-[lunch:ATE {meal: 'lunch'}]->(l:food)",
        "MATCH (u:User {id: $id})-[dinner:ATE {meal: 'dinner'}]->(d:food)",
        "WHERE date(breakfast.time) = date(lunch.time) = date(dinner.time)",
        "AND b.calories + l.calories + d.calories < u.calorieGoal",
        "MATCH ($meal)-[similarity:SIMILAR]->(resultFood:food)",
        "WHERE similarity.score > 0",
        "RETURN collect(resultFood)[0]"
    ]))
    

    db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))
    result = None
    with db_driver.session() as session:
        result = session.run(query, id=user_id, meal=meal)

    db_driver.close()
    return (result, 200)

if __name__ == '__main__':
    app.run()
