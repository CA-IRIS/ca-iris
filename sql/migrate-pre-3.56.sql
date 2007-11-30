SET SESSION AUTHORIZATION 'tms';

DROP TABLE font;
DROP TABLE character_list;
DROP TABLE character;

CREATE TABLE graphic (
	name TEXT PRIMARY KEY,
	bpp INTEGER NOT NULL DEFAULT 1,
	height INTEGER NOT NULL DEFAULT 7,
	width INTEGER NOT NULL DEFAULT 5,
	pixels TEXT NOT NULL DEFAULT 'AAAAAAA='
);
CREATE TABLE font (
	name TEXT PRIMARY KEY,
	height INTEGER NOT NULL DEFAULT 7,
	width INTEGER NOT NULL DEFAULT 0,
	line_spacing INTEGER NOT NULL DEFAULT 0,
	char_spacing INTEGER NOT NULL DEFAULT 0,
	version_id INTEGER NOT NULL DEFAULT 0
);
CREATE TABLE glyph (
	name TEXT PRIMARY KEY,
	font TEXT,
	code_point INTEGER NOT NULL DEFAULT 0,
	graphic TEXT
);
ALTER TABLE glyph
	ADD CONSTRAINT fk_glyph_font FOREIGN KEY (font) REFERENCES font(name);
ALTER TABLE glyph
	ADD CONSTRAINT fk_glyph_graphic FOREIGN KEY (graphic)
	REFERENCES graphic(name);

REVOKE ALL ON TABLE graphic FROM PUBLIC;
GRANT SELECT ON TABLE graphic TO PUBLIC;
REVOKE ALL ON TABLE font FROM PUBLIC;
GRANT SELECT ON TABLE font TO PUBLIC;
REVOKE ALL ON TABLE glyph FROM PUBLIC;
GRANT SELECT ON TABLE glyph TO PUBLIC;
