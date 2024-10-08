INSERT INTO organizations(id, name, address, postal_code, city) VALUES (1, 'organization 1', 'Rue du pont 42A', '1232', 'Peta ouschnock les bains');
INSERT INTO organizations(id, name, address, postal_code, city) VALUES (2, 'organization 2', 'Rue du pont 42A', '1232', 'Peta ouschnock les bains');
INSERT INTO business_entities(id, organization_id, name, address, postal_code, city) VALUES (1, 1, 'business entity 11', 'Rue du pont 42A', '1232', 'Peta ouschnock les bains');
INSERT INTO business_entities(id, organization_id, name, address, postal_code, city) VALUES (2, 2, 'business entity 12', 'Rue du pont 42A', '1232', 'Peta ouschnock les bains');
INSERT INTO branches(id, business_entity_id,name, address, postal_code, city) VALUES (1, 1, 'branch 111', 'Chemin de la terasse 1A', '1232', 'Peta ouschnock les bains');
INSERT INTO branches(id, business_entity_id,name, address, postal_code, city) VALUES (2, 2, 'branch 112', 'Chemin de la clairiere 10A', '1232', 'Peta ouschnock les bains');
INSERT INTO branches(id, business_entity_id,name, address, postal_code, city) VALUES (3, 2, 'branch 113', 'Voie centra 13A', '1432', 'A droite du mur');
INSERT INTO branches(id, business_entity_id,name, address, postal_code, city) VALUES (4, 1, 'branch 114', 'Rue du pont 42A', '1232', 'Peta ouschnock les bains');
INSERT INTO branches(id, business_entity_id,name, address, postal_code, city) VALUES (5, 1, 'branch 121', 'Rue du pont 42A', '1232', 'Peta ouschnock les bains');
INSERT INTO branches(id, business_entity_id, name, address, postal_code, city) VALUES (6, 2, 'branch 122', 'Rue du pont 42A', '1232', 'Peta ouschnock les bains');
ALTER SEQUENCE branches_seq restart with 500;
ALTER SEQUENCE business_entities_seq restart with 500;
ALTER SEQUENCE organizations_seq restart with 500;
