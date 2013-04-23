INDEX_FILE   = index.tex
PDF_FILE     = report.pdf
BUILD_DIR    = build

PDF_VIEWER   = xdg-open
RUBBER       = rubber
RUBBER_ARGS  = --inplace \
	       --into=$(BUILD_DIR) \
	       --maxerr=-1 \
	       --short \
	       --force \
	       --warn=all \
	       --pdf

INKSCAPE     = inkscape


FIGURES      = class-diagram
FIGURES_DIR  = figures


SVG_TO_PDF   = $(foreach x,$(FIGURES),$(FIGURES_DIR)/$(x).pdf)

%.pdf: %.svg
	@echo "Converting SVG to PDF"
	@inkscape --without-gui --file=$< --export-pdf=$@

build: $(SVG_TO_PDF)
	@echo "Building pdf from LaTeX files"
	@mkdir -p $(BUILD_DIR)
	$(RUBBER) $(RUBBER_ARGS) $(INDEX_FILE)
	@mv $(BUILD_DIR)/$(INDEX_FILE:.tex=.pdf) $(PDF_FILE)

view: build
	@echo "Viewing pdf"
	$(PDF_VIEWER) $(PDF_FILE)

clean:
	@echo "Removing LaTeX build files"
	@rm -rf $(BUILD_DIR)
	@rm $(SVG_TO_PDF)
