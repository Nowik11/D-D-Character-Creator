CREATE TABLE IF NOT EXISTS types(
                    id INT NOT NULL,
                    hit_dice INT NOT NULL,
                    name VARCHAR(128) NOT NULL ,
                    description VARCHAR(1024) NOT NULL
);

CREATE TABLE IF NOT EXISTS features(
    id INT NOT NULL ,
    type_id INT NOT NULL,
    required_level INT NOT NULL ,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(1024) NOT NULL
);